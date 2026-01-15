package com.ReZherk.microservice_auth.exception;

/**
 * * Custom exception to represent failures when calling external services
 * (e.g., via Feign).
 */
public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
