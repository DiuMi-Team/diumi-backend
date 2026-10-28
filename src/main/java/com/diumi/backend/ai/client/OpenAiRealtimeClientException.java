package com.diumi.backend.ai.client;

public class OpenAiRealtimeClientException extends RuntimeException {
    public OpenAiRealtimeClientException(String message) {
        super(message);
    }

    public OpenAiRealtimeClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
