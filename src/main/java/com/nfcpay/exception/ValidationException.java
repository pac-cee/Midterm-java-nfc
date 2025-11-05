package com.nfcpay.exception;

/**
 * Exception for validation errors
 */
public class ValidationException extends NFCPayException {
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message, message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super("VALIDATION_ERROR", message, message, cause);
    }
}