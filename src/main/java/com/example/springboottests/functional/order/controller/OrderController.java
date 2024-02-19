package com.example.springboottests.functional.order.controller;

import com.example.springboottests.functional.order.model.OrderDto;
import com.example.springboottests.functional.order.model.OrderRequest;
import com.example.springboottests.functional.order.service.OrderProcessor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for handling order-related requests.
 *
 * @author Georgii Lvov
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderProcessor orderProcessor;

    /**
     * Endpoint for updating an order.
     *
     * @param id            The ID of the order to update.
     * @param orderRequest  The order details for update.
     * @return              The updated order details.
     */
    @PatchMapping(
            path = "/v1/orders/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public OrderDto updateOrder(@PathVariable Long id, @RequestBody @Valid OrderRequest orderRequest) {
        return orderProcessor.updateOrder(id, orderRequest);
    }
}
