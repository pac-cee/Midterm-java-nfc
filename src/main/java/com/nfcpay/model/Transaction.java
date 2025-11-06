package com.nfcpay.model;

import com.nfcpay.model.enums.TransactionType;
import com.nfcpay.model.enums.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction Model Class
 * Matches database transactions table structure
 */
public class Transaction {
    private int transactionId;
    private int userId;
    private int cardId;
    private int merchantId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private TransactionStatus status;
    private String referenceCode;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    
    // Default constructor
    public Transaction() {}
    
    // Constructor for new transaction
    public Transaction(int userId, int cardId, int merchantId, BigDecimal amount, 
                      TransactionType transactionType, String referenceCode, String description) {
        this.userId = userId;
        this.cardId = cardId;
        this.merchantId = merchantId;
        this.amount = amount;
        this.transactionType = transactionType != null ? transactionType : TransactionType.PAYMENT;
        this.status = TransactionStatus.PENDING;
        this.referenceCode = referenceCode != null ? referenceCode : com.nfcpay.util.UIDGenerator.generateTransactionReference();
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor with string types
    public Transaction(int userId, int cardId, int merchantId, BigDecimal amount, 
                      String transactionType, String status, String description) {
        this.userId = userId;
        this.cardId = cardId;
        this.merchantId = merchantId;
        this.amount = amount;
        this.transactionType = TransactionType.valueOf(transactionType);
        this.status = TransactionStatus.valueOf(status);
        this.referenceCode = com.nfcpay.util.UIDGenerator.generateTransactionReference();
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
    
    // Full constructor
    public Transaction(int transactionId, int userId, int cardId, int merchantId, BigDecimal amount, 
                      TransactionType transactionType, TransactionStatus status, String referenceCode, 
                      String description, LocalDateTime createdAt, LocalDateTime processedAt) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.cardId = cardId;
        this.merchantId = merchantId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.status = status;
        this.referenceCode = referenceCode;
        this.description = description;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }
    
    // Getters and Setters
    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getCardId() { return cardId; }
    public void setCardId(int cardId) { this.cardId = cardId; }
    
    public int getMerchantId() { return merchantId; }
    public void setMerchantId(int merchantId) { this.merchantId = merchantId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }
    
    public String getType() { return transactionType != null ? transactionType.toString() : null; }
    
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    
    public void setStatus(String statusStr) {
        if (statusStr != null) {
            this.status = TransactionStatus.valueOf(statusStr);
        }
    }
    
    public String getReferenceCode() { return referenceCode; }
    public void setReferenceCode(String referenceCode) { 
        this.referenceCode = referenceCode != null ? referenceCode : com.nfcpay.util.UIDGenerator.generateTransactionReference();
    }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    
    // Business methods
    public void markAsSuccess() {
        this.status = TransactionStatus.SUCCESS;
        this.processedAt = LocalDateTime.now();
    }
    
    public void markAsFailed() {
        this.status = TransactionStatus.FAILED;
        this.processedAt = LocalDateTime.now();
    }
    
    public void markAsCancelled() {
        this.status = TransactionStatus.CANCELLED;
        this.processedAt = LocalDateTime.now();
    }
    
    public boolean isPending() { return status == TransactionStatus.PENDING; }
    public boolean isSuccessful() { return status == TransactionStatus.SUCCESS; }
    
    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }
    
    public String getStatusDisplay() {
        return status.toString();
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", userId=" + userId +
                ", cardId=" + cardId +
                ", merchantId=" + merchantId +
                ", amount=" + amount +
                ", transactionType=" + transactionType +
                ", status=" + status +
                ", referenceCode='" + referenceCode + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}