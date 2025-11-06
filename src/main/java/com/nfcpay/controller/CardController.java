package com.nfcpay.controller;

import com.nfcpay.service.CardService;
import com.nfcpay.model.Card;
import com.nfcpay.model.enums.CardType;
import com.nfcpay.exception.NFCPayException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Card Management Controller
 * Handles card operations through CardService
 */
public class CardController {
    private final CardService cardService;
    
    public CardController() {
        this.cardService = new CardService();
    }
    
    public Card addCard(int userId, String cardName, CardType cardType) throws NFCPayException {
        return cardService.addCard(userId, cardName, cardType);
    }
    
    public Card addCard(int userId, String cardName, CardType cardType, BigDecimal initialBalance) throws NFCPayException {
        return cardService.addCard(userId, cardName, cardType, initialBalance);
    }
    
    public void updateCard(int cardId, int userId, String cardName) throws NFCPayException {
        cardService.updateCard(cardId, userId, cardName);
    }
    
    public void activateCard(int cardId, int userId) throws NFCPayException {
        cardService.activateCard(cardId, userId);
    }
    
    public void deactivateCard(int cardId, int userId) throws NFCPayException {
        cardService.deactivateCard(cardId, userId);
    }
    
    public void deleteCard(int cardId, int userId) throws NFCPayException {
        cardService.deleteCard(cardId, userId);
    }
    
    public List<Card> getUserCards(int userId) throws NFCPayException {
        return cardService.getUserCards(userId);
    }
    
    public List<Card> getActiveCards(int userId) throws NFCPayException {
        return cardService.getActiveCards(userId);
    }
    
    public Card getCard(int cardId, int userId) throws NFCPayException {
        return cardService.getCard(cardId, userId);
    }
    
    public boolean canAddMoreCards(int userId) throws NFCPayException {
        return cardService.canAddMoreCards(userId);
    }
    
    public void validateCardForPayment(int cardId, int userId) throws NFCPayException {
        cardService.validateCardForPayment(cardId, userId);
    }
}