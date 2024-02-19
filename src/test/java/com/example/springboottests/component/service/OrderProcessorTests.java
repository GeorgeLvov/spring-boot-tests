package com.example.springboottests.component.service;

import com.example.springboottests.functional.order.mapper.OrderMapper;
import com.example.springboottests.functional.order.model.OrderDto;
import com.example.springboottests.functional.order.model.OrderEntity;
import com.example.springboottests.functional.order.model.OrderRequest;
import com.example.springboottests.functional.order.repository.OrderRepository;
import com.example.springboottests.functional.order.service.OrderProcessor;
import com.example.springboottests.functional.order.service.OrderService;
import com.example.springboottests.functional.order.service.WmstkNotificationSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Component tests for {@link OrderProcessor}.
 * <p>
 * These tests focus on verifying the behavior of the {@link OrderProcessor} class.
 * The {@code @ExtendWith(SpringExtension.class)} annotation is used to integrate with
 * the Spring test framework. The {@code @ContextConfiguration} annotation specifies
 * the component classes that need to be loaded into the test context.
 * <p>
 * Note: This test does not use {@link SpringBootTest}, so some functionality, such as reading values
 * from application.properties is not available during the test.
 * If they are required, they can be set with {@link ReflectionTestUtils} or
 * {@code @ExtendWith(SpringExtension.class)} can be replaced with {@code @SpringBootTest}
 *
 * @author Georgii Lvov
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {OrderProcessor.class, OrderService.class, OrderMapper.class})
class OrderProcessorTests {

    @Autowired
    private OrderProcessor orderProcessor;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private WmstkNotificationSender wmstkNotificationSender;

    @Captor
    private ArgumentCaptor<OrderDto> orderDtoCaptor;

    /**
     * Test case to verify that the {@link WmstkNotificationSender} is called with the updated order values.
     */
    @Test
    void testWmstkNotificationSenderIsCalledWithUpdatedOrderValues() {
        // arrange
        OrderEntity orderEntity = getOrderEntity();
        OrderDto expcetedOrderDto = new OrderDto(1L, "new_workorder_id_value", "new_state_value");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));

        // action
        orderProcessor.updateOrder(1L, new OrderRequest("new_workorder_id_value", "new_state_value"));

        // assert
        assertAll(
                () -> verify(wmstkNotificationSender).sendNotification(orderDtoCaptor.capture()),
                () -> assertThat(orderDtoCaptor.getValue())
                        .usingRecursiveComparison()
                        .isEqualTo(expcetedOrderDto)
        );
    }

    private static OrderEntity getOrderEntity() {
        OrderEntity orderEntity = new OrderEntity();

        orderEntity.setId(1L);
        orderEntity.setWorkorderId("old_workorder_id_value");
        orderEntity.setState("old_state_value");

        return orderEntity;
    }
}
