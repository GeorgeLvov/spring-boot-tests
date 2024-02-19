package com.example.springboottests.component.jpa;

import com.example.springboottests.functional.order.model.OrderEntity;
import com.example.springboottests.functional.order.repository.OrderRepository;
import com.example.springboottests.misc.config.PostgreSqlContainerConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Component tests for the {@link OrderRepository}.
 * <p>
 * These tests verify the functionality of the {@link OrderRepository} by interacting with
 * a real database. The database configuration is provided by {@link PostgreSqlContainerConfiguration}.
 * <p>
 * The {@code @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)} annotation
 * ensures that the test will use the configured database instance instead of replacing it with an
 * in-memory database. This is necessary to interact with a real database for testing purposes.
 * <p>
 * The {@link TestEntityManager} is used to interact with the database in tests, to configure
 * the initial state of the database or subsequently check the operation of the {@link OrderRepository}.
 *
 * @author Georgii Lvov
 */
@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PostgreSqlContainerConfiguration.class)
class OrderRepositoryTests {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    /**
     * Test case to verify successful saving of an order entity.
     */
    @Test
    void testSaveOrderSuccessful() {
        OrderEntity order = getOrderEntity("wo_id_1", "state_1");
        OrderEntity savedOrder = orderRepository.save(order);

        assertThat(savedOrder.getId()).isNotNull();
    }

    /**
     * Test case to verify successful retrieval of all orders.
     */
    @Test
    void testFindAllOrdersSuccessful() {
        testEntityManager.persist(getOrderEntity("wo_id_1", "state_1"));
        testEntityManager.persist(getOrderEntity("wo_id_2", "state_2"));

        List<OrderEntity> orders = orderRepository.findAll();

        assertThat(orders).hasSize(2);
    }

    private static OrderEntity getOrderEntity(String workorderId, String state) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setWorkorderId(workorderId);
        orderEntity.setState(state);
        return orderEntity;
    }
}
