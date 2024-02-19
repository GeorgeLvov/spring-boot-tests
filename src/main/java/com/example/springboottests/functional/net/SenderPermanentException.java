package com.example.springboottests.functional.net;

public class SenderPermanentException extends RuntimeException {
    public SenderPermanentException(String message, Throwable cause) {
        super(message, cause);
    }
}
