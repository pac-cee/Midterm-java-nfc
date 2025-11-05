package com.nfcpay.model;

import java.time.LocalDateTime;

/**
 * Merchant Model Class
 * Matches database merchants table structure
 */
public class Merchant {
    private int merchantId;
    private String merchantName;
    private String merchantCode;
    private String category;
    private boolean isActive;
    private LocalDateTime createdAt;
    
    // Default constructor
    public Merchant() {}
    
    // Constructor for new merchant
    public Merchant(String merchantName, String merchantCode, String category) {
        this.merchantName = merchantName;
        this.merchantCode = merchantCode;
        this.category = category != null ? category : "General";
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }
    
    // Full constructor
    public Merchant(int merchantId, String merchantName, String merchantCode, String category, 
                    boolean isActive, LocalDateTime createdAt) {
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.merchantCode = merchantCode;
        this.category = category;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public int getMerchantId() { return merchantId; }
    public void setMerchantId(int merchantId) { this.merchantId = merchantId; }
    
    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    
    public String getMerchantCode() { return merchantCode; }
    public void setMerchantCode(String merchantCode) { this.merchantCode = merchantCode; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // Business methods
    public void activate() { this.isActive = true; }
    public void deactivate() { this.isActive = false; }
    
    public boolean canAcceptPayments() { return isActive; }
    
    public String getDisplayName() {
        return merchantName + " [" + merchantCode + "]";
    }
    
    @Override
    public String toString() {
        return "Merchant{" +
                "merchantId=" + merchantId +
                ", merchantName='" + merchantName + '\'' +
                ", merchantCode='" + merchantCode + '\'' +
                ", category='" + category + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}