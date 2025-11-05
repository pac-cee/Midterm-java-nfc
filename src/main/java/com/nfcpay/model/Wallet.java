package com.nfcpay.model;

import com.nfcpay.model.enums.Currency;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Wallet Model Class
 * Matches database wallets table structure
 */
public class Wallet {
    private int walletId;
    private int userId;
    private BigDecimal balance;
    private Currency currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public Wallet() {}
    
    // Constructor for new wallet
    public Wallet(int userId, BigDecimal balance, Currency currency) {
        this.userId = userId;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.currency = currency != null ? currency : Currency.USD;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Full constructor
    public Wallet(int walletId, int userId, BigDecimal balance, Currency currency, 
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.walletId = walletId;
        this.userId = userId;
        this.balance = balance;
        this.currency = currency;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public int getWalletId() { return walletId; }
    public void setWalletId(int walletId) { this.walletId = walletId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    
    public Currency getCurrency() { return currency; }
    public void setCurrency(Currency currency) { this.currency = currency; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Business methods
    public boolean hasSufficientBalance(BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }
    
    public void addFunds(BigDecimal amount) {
        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void deductFunds(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getFormattedBalance() {
        return String.format("%.2f %s", balance, currency);
    }
    
    @Override
    public String toString() {
        return "Wallet{" +
                "walletId=" + walletId +
                ", userId=" + userId +
                ", balance=" + balance +
                ", currency=" + currency +
                ", updatedAt=" + updatedAt +
                '}';
    }
}