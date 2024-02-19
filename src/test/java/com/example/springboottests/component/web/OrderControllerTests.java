package com.example.springboottests.component.web;

import com.example.springboottests.functional.order.controller.OrderController;
import com.example.springboottests.functional.order.exception.OrderNotFoundException;
import com.example.springboottests.functional.order.model.OrderDto;
import com.example.springboottests.functional.order.service.OrderProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.springboottests.misc.utils.TestUtils.loadFile;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Component tests for the {@link OrderController}.
 * <p>
 * These tests focus on the behavior of the {@link OrderController} class by mocking the
 * {@link OrderProcessor} and interacting with the controller endpoints. The {@code @WebMvcTest(OrderController.class)}
 * annotation is used to narrow the testing scope to the web layer. {@link SecurityFilterChain} is mocked to
 * bypass security configurations.
 * <p>
 * Note: {@link SecurityFilterChain} is mocked here to demonstrate mocking of security configuration,
 * and also to emphasize that security functionality is tested separately in {@link SecurityTests}.
 *
 * @author Georgii Lvov
 */
@WebMvcTest(controllers = OrderController.class)
@MockBean(SecurityFilterChain.class)
class OrderControllerTests {

    private static final String ORDERS_URI_WITH_ID = "/v1/orders/1";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderProcessor orderProcessor;

    /**
     * Test case to verify that when an order is processed successfully,
     * the response status is OK (200) and the correct order details are returned.
     *
     */
    @Test
    void testWhenOrderProcessedSuccessfullyThenStatusIsOk() throws Exception {
        String requestBody = loadFile("/data/json/orderRequest.json");
        String expectedResponseBody = loadFile("/data/json/orderDto.json");

        when(orderProcessor.updateOrder(any(), any()))
                .thenReturn(getOrderDto());

        String actualResponseBody = mockMvc.perform(patch(ORDERS_URI_WITH_ID)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(actualResponseBody).isEqualTo(expectedResponseBody);
    }

    /**
     * Test case to verify that when the workorder ID is null,
     * the response status is BAD_REQUEST (400) and the appropriate error message is returned.
     *
     */
    @Test
    void testWhenWorkorderIdIsNullThenStatusIsBadRequest() throws Exception {
        String requestBody = loadFile("/data/json/orderRequestWorkorderIdIsNull.json");
        String expectedResponseBody = loadFile("/data/json/apiErrorBadRequest.json");

        String actualResponseBody = mockMvc.perform(patch(ORDERS_URI_WITH_ID)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(actualResponseBody).isEqualTo(expectedResponseBody);
    }

    /**
     * Test case to verify that when the order processor throws an {@link OrderNotFoundException},
     * the response status is NOT_FOUND (404) and the appropriate error message is returned.
     *
     */
    @Test
    void testWhenOrderProcessorThrowsOrderNotFoundThenStatusIsNotFound() throws Exception {
        String requestBody = loadFile("/data/json/orderRequest.json");
        String expectedResponseBody = loadFile("/data/json/apiErrorNotFound.json");

        when(orderProcessor.updateOrder(any(), any()))
                .thenThrow(new OrderNotFoundException("No order found with id=1"));

        String actualResponseBody = mockMvc.perform(patch(ORDERS_URI_WITH_ID)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(actualResponseBody).isEqualTo(expectedResponseBody);
    }

    private static OrderDto getOrderDto() {
        return new OrderDto(
                1L,
                "new_workorder_id_value",
                "new_state_value"
        );
    }
}
