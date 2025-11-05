package com.nfcpay.service;

import com.nfcpay.exception.ValidationException;
import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Centralized validation service for all business and technical validations
 */
public class ValidationService {
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    // Card number pattern (16 digits)
    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("^\\d{16}$");
    
    // Merchant code pattern (3-10 alphanumeric)
    private static final Pattern MERCHANT_CODE_PATTERN = Pattern.compile("^[A-Z0-9]{3,10}$");
    
    // Business Validations
    
    public static void validateEmail(String email) throws ValidationException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Invalid email format");
        }
    }
    
    public static void validatePassword(String password) throws ValidationException {
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password is required");
        }
        if (password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new ValidationException("Password must contain at least one digit");
        }
    }
    
    public static void validateAmount(BigDecimal amount) throws ValidationException {
        if (amount == null) {
            throw new ValidationException("Amount is required");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }
        if (amount.compareTo(new BigDecimal("10000")) > 0) {
            throw new ValidationException("Amount cannot exceed $10,000 per transaction");
        }
    }
    
    public static void validateCardNumber(String cardNumber) throws ValidationException {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            throw new ValidationException("Card number is required");
        }
        if (!CARD_NUMBER_PATTERN.matcher(cardNumber.trim()).matches()) {
            throw new ValidationException("Card number must be exactly 16 digits");
        }
    }
    
    public static void validateMerchantCode(String merchantCode) throws ValidationException {
        if (merchantCode == null || merchantCode.trim().isEmpty()) {
            throw new ValidationException("Merchant code is required");
        }
        if (!MERCHANT_CODE_PATTERN.matcher(merchantCode.trim().toUpperCase()).matches()) {
            throw new ValidationException("Merchant code must be 3-10 alphanumeric characters");
        }
    }
    
    // Technical Validations
    
    public static void validateNotNull(Object obj, String fieldName) throws ValidationException {
        if (obj == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }
    }
    
    public static void validateStringLength(String str, String fieldName, int minLength, int maxLength) throws ValidationException {
        if (str == null) {
            throw new ValidationException(fieldName + " is required");
        }
        if (str.trim().length() < minLength) {
            throw new ValidationException(fieldName + " must be at least " + minLength + " characters");
        }
        if (str.trim().length() > maxLength) {
            throw new ValidationException(fieldName + " cannot exceed " + maxLength + " characters");
        }
    }
    
    public static void validatePositiveInteger(Integer value, String fieldName) throws ValidationException {
        if (value == null || value <= 0) {
            throw new ValidationException(fieldName + " must be a positive integer");
        }
    }
    
    public static void validateRange(int value, String fieldName, int min, int max) throws ValidationException {
        if (value < min || value > max) {
            throw new ValidationException(fieldName + " must be between " + min + " and " + max);
        }
    }
    
    public static void validateBoolean(Boolean value, String fieldName) throws ValidationException {
        if (value == null) {
            throw new ValidationException(fieldName + " must be specified");
        }
    }
}