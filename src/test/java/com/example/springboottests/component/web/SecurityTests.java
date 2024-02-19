package com.example.springboottests.component.web;

import com.example.springboottests.component.web.SecurityTestsConfiguration.TestController;
import com.example.springboottests.functional.order.controller.OrderController;
import com.example.springboottests.misc.annotations.AutoConfigureWiremockAuthorizationServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.example.springboottests.misc.utils.SecurityTestsUtils.generateJwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Component security tests for {@link TestController}.
 * <p>
 * These tests verify the security configurations applied to the secured endpoints.
 * The {@code @WebMvcTest(TestController.class)} annotation focuses the test only on the web layer of the
 * {@link TestController}, and the {@code @AutoConfigureWiremockAuthorizationServer} annotation ensures
 * that the OAuth2 authorization server is mocked using WireMock.
 * <p>
 * Note: These tests were originally intended to be merged with {@link OrderControllerTests}. They were
 * separated for semantic clarity, allowing separate testing of security functionality without cluttering
 * the test class for {@link OrderController}.
 *
 * @author Georgii Lvov
 */
@WebMvcTest(TestController.class)
@AutoConfigureWiremockAuthorizationServer
@Import(SecurityTestsConfiguration.class)
class SecurityTests {

    private static final String AUTH_TEST_ENDPOINT = "/auth/test";

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test case to verify that a request with a valid JWT token results in a successful status.
     */
    @Test
    void testGivenValidJwtWhenRequestThenStatusIsOk() throws Exception {
        String token = generateJwt(
                issuerUri,
                HttpMethod.GET.name()
        );

        sendRequestWithToken(token)
                .andExpect(status().isOk());
    }

    /**
     * Test case to verify that a request without a JWT token results in an unauthorized status.
     */
    @Test
    void testGivenJwtMissingWhenRequestThenStatusIsUnauthorized() throws Exception {
        this.mockMvc
                .perform(get(AUTH_TEST_ENDPOINT))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test case to verify that a request with a JWT token with an invalid issuer claim
     * results in an unauthorized status.
     */
    @Test
    void testGivenJwtWithInvalidIssuerClaimWhenRequestThenStatusIsUnauthorized() throws Exception {
        String token = generateJwt(
                "http://localhost:8085/invalid_issuer_uri",
                HttpMethod.GET.name()
        );

        sendRequestWithToken(token)
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test case to verify that a request with a JWT token with an invalid operation claim
     * results in an unauthorized status.
     */
    @Test
    void testGivenJwtWithInvalidOperationClaimWhenGetRequestThenStatusIsUnauthorized() throws Exception {
        String token = generateJwt(
                issuerUri,
                HttpMethod.POST.name()
        );

        sendRequestWithToken(token)
                .andExpect(status().isUnauthorized());
    }

    private ResultActions sendRequestWithToken(String token) throws Exception {
        return this.mockMvc
                .perform(
                        get(AUTH_TEST_ENDPOINT)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                );
    }
}
