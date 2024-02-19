package com.example.springboottests.unit;

import com.example.springboottests.functional.net.SenderPermanentException;
import com.example.springboottests.functional.order.exception.OrderNotFoundException;
import com.example.springboottests.functional.order.exception.OrderProcessingException;
import com.example.springboottests.functional.order.model.OrderDto;
import com.example.springboottests.functional.order.model.OrderRequest;
import com.example.springboottests.functional.order.service.OrderProcessor;
import com.example.springboottests.functional.order.service.OrderService;
import com.example.springboottests.functional.order.service.WmstkNotificationSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link OrderProcessor}.
 * <p>
 * These tests focus on verifying the behavior of the {@link OrderProcessor} class in isolation.
 * The {@code @ExtendWith(MockitoExtension.class)} annotation is used to integrate with
 * the Mockito test framework.
 *
 * @author Georgii Lvov
 */
@ExtendWith(MockitoExtension.class)
class OrderProcessorTests {

    @InjectMocks
    private OrderProcessor orderProcessor;

    @Mock
    private OrderService orderService;

    @Mock
    private WmstkNotificationSender wmstkNotificationSender;

    /**
     * Test case to verify that the {@link OrderProcessor} successfully returns an {@link OrderDto}.
     */
    @Test
    void testOrderProcessorSuccessfullyReturnsOrderDto() {
        OrderRequest orderRequest = getOrderRequest();
        OrderDto expectedOrderDto = getOrderDto();

        when(orderService.updateOrder(any(), any())).thenReturn(expectedOrderDto);

        OrderDto actualOrderDto = orderProcessor.updateOrder(1L, orderRequest);

        assertEquals(expectedOrderDto, actualOrderDto);
    }

    /**
     * Test case to verify that when the {@link OrderService} throws an {@link OrderNotFoundException},
     * the {@link OrderProcessor} also throws an {@link OrderNotFoundException}.
     */
    @Test
    void testWhenOrderServiceThrowsExceptionThenOrderProcessorThrowsException() {
        OrderRequest orderRequest = getOrderRequest();

        when(orderService.updateOrder(any(), any())).thenThrow(OrderNotFoundException.class);

        assertThrows(
                OrderNotFoundException.class,
                () -> orderProcessor.updateOrder(1L, orderRequest)
        );
    }

    /**
     * Test case to verify that when the {@link WmstkNotificationSender} throws a {@link SenderPermanentException},
     * the {@link OrderProcessor} throws an {@link OrderProcessingException}.
     */
    @Test
    void testWhenWmstkNotificationSenderThrowsExceptionThenOrderProcessorThrowsException() {
        OrderRequest orderRequest = getOrderRequest();

        doThrow(SenderPermanentException.class).when(wmstkNotificationSender).sendNotification(any());

        assertThrows(
                OrderProcessingException.class,
                () -> orderProcessor.updateOrder(1L, orderRequest)
        );
    }

    private static OrderRequest getOrderRequest() {
        return new OrderRequest("new_workorderId_value", "new_status");
    }

    private static OrderDto getOrderDto() {
        return new OrderDto(
                1L,
                "new_workorderId_value",
                "new_status"
        );
    }
}
