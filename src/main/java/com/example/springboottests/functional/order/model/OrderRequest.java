package com.example.springboottests.functional.order.model;

import jakarta.validation.constraints.NotBlank;

public record OrderRequest(
        @NotBlank
        String workorderId,
        @NotBlank
        String state) {
}
