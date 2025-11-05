package com.nfcpay.exception;

/**
 * Exception for payment processing errors
 */
public class PaymentException extends NFCPayException {
    public PaymentException(String errorCode, String message, String userMessage) {
        super(errorCode, message, userMessage);
    }
    
    public PaymentException(String errorCode, String message, String userMessage, Throwable cause) {
        super(errorCode, message, userMessage, cause);
    }
}