package com.example.springboottests.functional.order.model;

public record OrderDto(
        Long id,
        String workorderId,
        String state) {
}
