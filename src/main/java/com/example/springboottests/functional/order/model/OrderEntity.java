package com.example.springboottests.functional.order.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workorder_id", nullable = false, unique = true)
    private String workorderId;

    @Column(name = "state", nullable = false)
    private String state;
}
