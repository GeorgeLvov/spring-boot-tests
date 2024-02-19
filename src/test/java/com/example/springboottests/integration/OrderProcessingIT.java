package com.example.springboottests.integration;

import com.example.springboottests.functional.order.model.OrderEntity;
import com.example.springboottests.integration.config.WiremockStubsLoader;
import com.example.springboottests.misc.annotations.IntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.example.springboottests.misc.utils.SecurityTestsUtils.generateJwt;
import static com.example.springboottests.misc.utils.TestUtils.loadFile;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

/**
 * Integration tests for order processing functionality.
 * <p>
 * These tests validate the behavior of order processing in a complete environment,
 * including database operations, retry-functionality and WireMock-stubbed requests to dependent services.
 *
 * @author Georgii Lvov
 */
@IntegrationTest
class OrderProcessingIT {

    private static final String ORDERS_URI_WITH_ID = "/v1/orders/1";
    private static final String WMSTK_NOTIFICATION_URL = "/notification";
    private static final int AWAIT_TIMEOUT_SECONDS = 5;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WiremockStubsLoader wiremockStubsLoader;

    @Autowired
    private EntityManager entityManager;

    /**
     * Test case to verify that an order is successfully updated, correct response is returned,
     * and a notification ot wmstk-service has been sent.
     * This test case also tests the retry functionality, where the Wmstk-service responds with
     * two 412 statuses before finally responding with a 200 status.
     */
    @Test
    @Sql("/data/sql/insertOneOrder.sql")
    @Sql(scripts = "/data/sql/truncateTable.sql", executionPhase = AFTER_TEST_METHOD)
    void testOrderSuccessfullyUpdated() {
        // arrange
        wiremockStubsLoader.loadStubs(
                loadFile("/data/json/wiremock/stubs/oauth2ClientToken.json"),
                loadFile("/data/json/wiremock/stubs/wmstkNotificationWithRetry.json")
        );

        String requestBody = loadFile("/data/json/orderRequest.json");
        String expectedResponseBody = loadFile("/data/json/orderDto.json");
        OrderEntity expectedOrderEntity = getOrderEntity();

        // act
        String actualResponseBody = webTestClient.patch()
                .uri(ORDERS_URI_WITH_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, token())
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        OrderEntity actualOrderEntity = entityManager.find(OrderEntity.class, 1L);

        // assert
        await().atMost(AWAIT_TIMEOUT_SECONDS, SECONDS)
                .untilAsserted(() ->
                        assertAll(
                                () -> assertThatJson(actualResponseBody).isEqualTo(expectedResponseBody),
                                () -> assertThat(actualOrderEntity)
                                        .usingRecursiveComparison()
                                        .isEqualTo(expectedOrderEntity),
                                () -> verify(
                                        exactly(3),
                                        postRequestedFor(urlPathEqualTo(WMSTK_NOTIFICATION_URL))
                                                .withRequestBody(equalToJson(loadFile("/data/json/wmstkNotification.json")))
                                )
                        )
                );
    }

    /**
     * Test case to verify that an order is not found in the DB
     */
    @Test
    void testOrderIsNotFound() {
        String requestBody = loadFile("/data/json/orderRequest.json");
        String expectedResponseBody = loadFile("/data/json/apiErrorNotFound.json");

        String actualResponseBody = webTestClient.patch()
                .uri(ORDERS_URI_WITH_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, token())
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        await().atMost(AWAIT_TIMEOUT_SECONDS, SECONDS)
                .untilAsserted(() ->
                        assertAll(
                                () -> assertThatJson(actualResponseBody).isEqualTo(expectedResponseBody),
                                () -> verify(
                                        exactly(0),
                                        postRequestedFor(urlPathEqualTo(WMSTK_NOTIFICATION_URL))
                                )
                        )
                );
    }

    private static OrderEntity getOrderEntity() {
        OrderEntity orderEntity = new OrderEntity();

        orderEntity.setId(1L);
        orderEntity.setWorkorderId("new_workorder_id_value");
        orderEntity.setState("new_state_value");

        return orderEntity;
    }

    private String token() {
        return "Bearer " + generateJwt(issuerUri, HttpMethod.PATCH.name());
    }
}
