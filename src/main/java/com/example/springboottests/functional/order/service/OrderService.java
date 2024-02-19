package com.example.springboottests.functional.order.service;

import com.example.springboottests.functional.order.exception.OrderNotFoundException;
import com.example.springboottests.functional.order.mapper.OrderMapper;
import com.example.springboottests.functional.order.model.OrderDto;
import com.example.springboottests.functional.order.model.OrderEntity;
import com.example.springboottests.functional.order.model.OrderRequest;
import com.example.springboottests.functional.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling order-related operations.
 *
 * @author Georgii Lvov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    /**
     * Updates an order with the given ID using the information from the order request.
     *
     * @param id            The ID of the order to update.
     * @param orderRequest  The request containing the updated order details.
     * @return              The updated order DTO.
     * @throws OrderNotFoundException if no order is found with the given ID.
     */
    @Transactional
    public OrderDto updateOrder(Long id, OrderRequest orderRequest) {
        OrderEntity foundEntity = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("No order found with id=" + id));

        log.debug("Found entity to update: {}", foundEntity);

        orderMapper.updateEntityFromRequest(foundEntity, orderRequest);

        return orderMapper.toDto(foundEntity);
    }
}
