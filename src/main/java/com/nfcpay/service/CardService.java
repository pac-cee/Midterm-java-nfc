package com.nfcpay.service;

import com.nfcpay.dao.CardDAO;
import com.nfcpay.dao.UserDAO;
import com.nfcpay.model.Card;
import com.nfcpay.model.User;
import com.nfcpay.model.enums.CardType;
import com.nfcpay.exception.ValidationException;
import com.nfcpay.exception.NFCPayException;
import java.util.List;
import java.util.Random;

/**
 * Card Management Service
 */
public class CardService {
    private final CardDAO cardDAO;
    private final UserDAO userDAO;
    private final Random random;
    
    public CardService() {
        this.cardDAO = new CardDAO();
        this.userDAO = new UserDAO();
        this.random = new Random();
    }
    
    /**
     * Add new card with validation
     */
    public Card addCard(int userId, String cardName, CardType cardType) throws NFCPayException {
        ValidationService.validatePositiveInteger(userId, "User ID");
        ValidationService.validateStringLength(cardName, "Card name", 2, 50);
        ValidationService.validateNotNull(cardType, "Card type");
        
        // Validate user exists and is active
        User user = userDAO.getUserById(userId);
        if (user == null || !user.isActive()) {
            throw new ValidationException("User account is not active");
        }
        
        // Check card limit (max 5 cards per user)
        if (!cardDAO.canAddMoreCards(userId)) {
            throw new ValidationException("Maximum 5 cards allowed per user");
        }
        
        // Check for duplicate card names
        List<Card> userCards = cardDAO.getCardsByUserId(userId);
        for (Card existingCard : userCards) {
            if (existingCard.getCardName().equalsIgnoreCase(cardName.trim())) {
                throw new ValidationException("Card name already exists. Please choose a different name.");
            }
        }
        
        // Generate unique card UID
        String cardUid;
        do {
            cardUid = generateCardUid();
        } while (cardDAO.cardUidExists(cardUid));
        
        // Create card
        Card card = new Card(userId, cardUid, cardName.trim(), cardType);
        boolean created = cardDAO.createCard(card);
        
        if (!created) {
            throw new ValidationException("Failed to create card");
        }
        
        return card;
    }
    
    /**
     * Update card information
     */
    public void updateCard(int cardId, int userId, String cardName) throws NFCPayException {
        ValidationService.validatePositiveInteger(cardId, "Card ID");
        ValidationService.validatePositiveInteger(userId, "User ID");
        ValidationService.validateStringLength(cardName, "Card name", 2, 50);
        
        Card card = validateCardOwnership(cardId, userId);
        
        // Check for duplicate card names (excluding current card)
        List<Card> userCards = cardDAO.getCardsByUserId(userId);
        for (Card existingCard : userCards) {
            if (existingCard.getCardId() != cardId && 
                existingCard.getCardName().equalsIgnoreCase(cardName.trim())) {
                throw new ValidationException("Card name already exists. Please choose a different name.");
            }
        }
        
        card.setCardName(cardName.trim());
        boolean updated = cardDAO.updateCard(card);
        
        if (!updated) {
            throw new ValidationException("Failed to update card");
        }
    }
    
    /**
     * Activate card
     */
    public void activateCard(int cardId, int userId) throws NFCPayException {
        Card card = validateCardOwnership(cardId, userId);
        
        if (card.isActive()) {
            throw new ValidationException("Card is already active");
        }
        
        boolean activated = cardDAO.activateCard(cardId);
        if (!activated) {
            throw new ValidationException("Failed to activate card");
        }
    }
    
    /**
     * Deactivate card
     */
    public void deactivateCard(int cardId, int userId) throws NFCPayException {
        Card card = validateCardOwnership(cardId, userId);
        
        if (!card.isActive()) {
            throw new ValidationException("Card is already deactivated");
        }
        
        boolean deactivated = cardDAO.deactivateCard(cardId);
        if (!deactivated) {
            throw new ValidationException("Failed to deactivate card");
        }
    }
    
    /**
     * Delete card
     */
    public void deleteCard(int cardId, int userId) throws NFCPayException {
        validateCardOwnership(cardId, userId);
        
        boolean deleted = cardDAO.deleteCard(cardId);
        if (!deleted) {
            throw new ValidationException("Failed to delete card");
        }
    }
    
    /**
     * Get user's cards
     */
    public List<Card> getUserCards(int userId) throws NFCPayException {
        ValidationService.validatePositiveInteger(userId, "User ID");
        return cardDAO.getCardsByUserId(userId);
    }
    
    /**
     * Get active cards only
     */
    public List<Card> getActiveCards(int userId) throws NFCPayException {
        ValidationService.validatePositiveInteger(userId, "User ID");
        return cardDAO.getActiveCardsByUserId(userId);
    }
    
    /**
     * Get card by ID with ownership validation
     */
    public Card getCard(int cardId, int userId) throws NFCPayException {
        return validateCardOwnership(cardId, userId);
    }
    
    /**
     * Check if user can add more cards
     */
    public boolean canAddMoreCards(int userId) throws NFCPayException {
        return cardDAO.canAddMoreCards(userId);
    }
    
    /**
     * Validate card ownership
     */
    private Card validateCardOwnership(int cardId, int userId) throws NFCPayException {
        ValidationService.validatePositiveInteger(cardId, "Card ID");
        ValidationService.validatePositiveInteger(userId, "User ID");
        
        Card card = cardDAO.getCardById(cardId);
        if (card == null) {
            throw new ValidationException("Card not found");
        }
        
        if (!cardDAO.cardBelongsToUser(cardId, userId)) {
            throw new ValidationException("You are not authorized to access this card");
        }
        
        return card;
    }
    
    /**
     * Generate random 16-digit card UID
     */
    private String generateCardUid() {
        StringBuilder cardUid = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            cardUid.append(random.nextInt(10));
        }
        return cardUid.toString();
    }
    
    /**
     * Validate card for payment
     */
    public void validateCardForPayment(int cardId, int userId) throws NFCPayException {
        Card card = validateCardOwnership(cardId, userId);
        
        if (!card.isActive()) {
            throw new ValidationException("Card is deactivated and cannot be used for payments");
        }
    }
}