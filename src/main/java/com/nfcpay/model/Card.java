package com.nfcpay.model;

import com.nfcpay.model.enums.CardType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Card Model Class
 * Matches database cards table structure
 */
public class Card {
    private int cardId;
    private int userId;
    private String cardUid;
    private String cardName;
    private CardType cardType;
    private boolean isActive;
    private LocalDateTime createdAt;
    
    // Default constructor
    public Card() {}
    
    // Constructor for new card
    public Card(int userId, String cardUid, String cardName, CardType cardType) {
        this.userId = userId;
        this.cardUid = cardUid;
        this.cardName = cardName;
        this.cardType = cardType != null ? cardType : CardType.VIRTUAL;
        this.balance = BigDecimal.ZERO;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor for new card with balance
    public Card(int userId, String cardUid, String cardName, CardType cardType, BigDecimal balance) {
        this.userId = userId;
        this.cardUid = cardUid;
        this.cardName = cardName;
        this.cardType = cardType != null ? cardType : CardType.VIRTUAL;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }
    
    // Full constructor
    public Card(int cardId, int userId, String cardUid, String cardName, CardType cardType, 
                BigDecimal balance, boolean isActive, LocalDateTime createdAt) {
        this.cardId = cardId;
        this.userId = userId;
        this.cardUid = cardUid;
        this.cardName = cardName;
        this.cardType = cardType;
        this.balance = balance;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public int getCardId() { return cardId; }
    public void setCardId(int cardId) { this.cardId = cardId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getCardUid() { return cardUid; }
    public void setCardUid(String cardUid) { this.cardUid = cardUid; }
    
    public String getCardName() { return cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }
    
    public CardType getCardType() { return cardType; }
    public void setCardType(CardType cardType) { this.cardType = cardType; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // Business methods
    public void activate() { this.isActive = true; }
    public void deactivate() { this.isActive = false; }
    
    public boolean canProcessPayment() { return isActive; }
    
    public String getDisplayName() {
        return cardName + " (" + cardType + ")";
    }
    
    // Additional methods for UI compatibility
    public String getCardNumber() {
        return cardUid; // Using cardUid as card number
    }
    
    public void setCardNumber(String cardNumber) {
        this.cardUid = cardNumber;
    }
    
    private BigDecimal balance;
    
    public BigDecimal getBalance() {
        return balance != null ? balance : BigDecimal.ZERO;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    @Override
    public String toString() {
        return "Card{" +
                "cardId=" + cardId +
                ", userId=" + userId +
                ", cardUid='" + cardUid + '\'' +
                ", cardName='" + cardName + '\'' +
                ", cardType=" + cardType +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}