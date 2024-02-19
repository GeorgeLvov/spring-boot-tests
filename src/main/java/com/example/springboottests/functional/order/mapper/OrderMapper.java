package com.example.springboottests.functional.order.mapper;

import com.example.springboottests.functional.order.model.OrderDto;
import com.example.springboottests.functional.order.model.OrderEntity;
import com.example.springboottests.functional.order.model.OrderRequest;
import org.springframework.stereotype.Component;

/**
 * Mapper component for converting between order entities, DTOs, and request objects.
 *
 * @author Georgii Lvov
 */
@Component
public class OrderMapper {

    /**
     * Updates the provided entity with data from the given order request.
     *
     */
    public void updateEntityFromRequest(OrderEntity entity, OrderRequest orderRequest){
        if (orderRequest == null) {
            return;
        }

        entity.setWorkorderId(orderRequest.workorderId());
        entity.setState(orderRequest.state());
    }

    /**
     * Converts the provided entity to a DTO.
     *
     */
    public OrderDto toDto(OrderEntity entity) {
        return new OrderDto(
                entity.getId(),
                entity.getWorkorderId(),
                entity.getState()
        );
    }
}
