package com.example.springboottests.functional.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record ApiError(
        int status,
        String reasonPhrase,
        String errorMessage,
        @JsonProperty("timestamp")
        LocalDateTime localDateTime) {
}
