package com.nfcpay.exception;

/**
 * Base exception class for NFC Payment System
 */
public class NFCPayException extends Exception {
    private final String errorCode;
    private final String userMessage;
    
    public NFCPayException(String errorCode, String message, String userMessage) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    public NFCPayException(String errorCode, String message, String userMessage, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    public String getErrorCode() { return errorCode; }
    public String getUserMessage() { return userMessage; }
}