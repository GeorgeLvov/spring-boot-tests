package com.example.springboottests.functional.net;

public class PreConditionException extends RuntimeException {
    public PreConditionException(String message, Throwable cause) {
        super(message, cause);
    }
}
