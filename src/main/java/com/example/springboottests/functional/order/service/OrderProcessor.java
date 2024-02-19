package com.example.springboottests.functional.order.service;

import com.example.springboottests.functional.order.exception.OrderNotFoundException;
import com.example.springboottests.functional.order.exception.OrderProcessingException;
import com.example.springboottests.functional.order.model.OrderDto;
import com.example.springboottests.functional.order.model.OrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for processing order updates.
 *
 * @author Georgii Lvov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProcessor {

    private final OrderService orderService;
    private final WmstkNotificationSender wmstkNotificationSender;

    /**
     * Updates an order with the given ID and request data.
     * If the order is successfully updated, a notification is sent to WMSTK-Service.
     *
     * @param id           The ID of the order to update.
     * @param orderRequest The request data for updating the order.
     * @return The updated order DTO.
     * @throws OrderNotFoundException   If the order with the specified ID is not found.
     * @throws OrderProcessingException If an error occurs while processing the order.
     */
    public OrderDto updateOrder(Long id, OrderRequest orderRequest) {
        try {
            OrderDto orderDto = orderService.updateOrder(id, orderRequest);

            log.info("Order successfully updated!");

            wmstkNotificationSender.sendNotification(orderDto);

            return orderDto;
        } catch (OrderNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderProcessingException("Failed to process order!", e);
        }
    }
}
