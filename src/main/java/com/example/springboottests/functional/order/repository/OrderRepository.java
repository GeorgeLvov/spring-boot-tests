package com.example.springboottests.functional.order.repository;

import com.example.springboottests.functional.order.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing order entities.
 */
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
