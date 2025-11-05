package com.nfcpay.service;

import com.nfcpay.dao.*;
import com.nfcpay.model.*;
import com.nfcpay.model.enums.*;
import com.nfcpay.exception.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

/**
 * Payment Processing Service with comprehensive business logic
 */
public class PaymentService {
    private final TransactionDAO transactionDAO;
    private final CardDAO cardDAO;
    private final WalletDAO walletDAO;
    private final MerchantDAO merchantDAO;
    private final UserDAO userDAO;
    
    public PaymentService() {
        this.transactionDAO = new TransactionDAO();
        this.cardDAO = new CardDAO();
        this.walletDAO = new WalletDAO();
        this.merchantDAO = new MerchantDAO();
        this.userDAO = new UserDAO();
    }
    
    /**
     * Process NFC payment with comprehensive validation
     */
    public Transaction processPayment(int userId, int cardId, int merchantId, BigDecimal amount, String description) throws NFCPayException {
        // Validate inputs
        ValidationService.validatePositiveInteger(userId, "User ID");
        ValidationService.validatePositiveInteger(cardId, "Card ID");
        ValidationService.validatePositiveInteger(merchantId, "Merchant ID");
        ValidationService.validateAmount(amount);
        
        // Validate user exists and is active
        User user = userDAO.getUserById(userId);
        if (user == null || !user.isActive()) {
            throw new PaymentException("USER_INACTIVE", "User account is inactive", "Your account is not active. Please contact support.");
        }
        
        // Validate card exists, is active, and belongs to user
        Card card = cardDAO.getCardById(cardId);
        if (card == null) {
            throw new PaymentException("CARD_NOT_FOUND", "Card not found", "Selected card is not available");
        }
        if (!card.isActive()) {
            throw new PaymentException("CARD_INACTIVE", "Card is inactive", "This card is currently deactivated");
        }
        if (!cardDAO.cardBelongsToUser(cardId, userId)) {
            throw new PaymentException("CARD_UNAUTHORIZED", "Card does not belong to user", "You are not authorized to use this card");
        }
        
        // Validate merchant exists and is active
        Merchant merchant = merchantDAO.getMerchantById(merchantId);
        if (merchant == null || !merchant.isActive()) {
            throw new PaymentException("MERCHANT_INACTIVE", "Merchant is inactive", "This merchant is currently not accepting payments");
        }
        
        // Check sufficient balance
        if (!walletDAO.hasSufficientBalance(userId, amount)) {
            BigDecimal balance = walletDAO.getBalance(userId);
            throw new PaymentException("INSUFFICIENT_FUNDS", "Insufficient wallet balance", 
                String.format("Insufficient funds. Available: $%.2f, Required: $%.2f", balance, amount));
        }
        
        // Check daily transaction limit
        BigDecimal dailySpent = transactionDAO.getDailySpent(userId);
        BigDecimal dailyLimit = new BigDecimal("5000"); // $5000 daily limit
        if (dailySpent.add(amount).compareTo(dailyLimit) > 0) {
            throw new PaymentException("DAILY_LIMIT_EXCEEDED", "Daily transaction limit exceeded", 
                String.format("Daily limit exceeded. Limit: $%.2f, Already spent: $%.2f", 
                    dailyLimit, dailySpent));
        }
        
        // Create transaction
        Transaction transaction = new Transaction(
            userId, cardId, merchantId, amount, 
            "PAYMENT", "PENDING", 
            description != null ? description : "NFC Payment"
        );
        
        try {
            // Deduct from wallet
            boolean deducted = walletDAO.deductFunds(userId, amount);
            if (!deducted) {
                throw new PaymentException("PAYMENT_FAILED", "Failed to deduct funds", "Payment could not be processed.");
            }
            
            // Save transaction as completed
            transaction.setStatus("SUCCESS");
            transaction.setProcessedAt(LocalDateTime.now());
            boolean saved = transactionDAO.createTransaction(transaction);
            
            if (!saved) {
                // Rollback - add funds back
                walletDAO.addFunds(userId, amount);
                throw new PaymentException("PAYMENT_FAILED", "Failed to save transaction", "Payment could not be processed.");
            }
            
            return transaction;
            
        } catch (Exception e) {
            // Create failed transaction record
            transaction.setStatus("FAILED");
            transaction.setProcessedAt(LocalDateTime.now());
            transactionDAO.createTransaction(transaction);
            
            throw new PaymentException("PAYMENT_FAILED", "Payment processing failed", 
                "Payment could not be processed. Please try again.");
        }
    }
    
    /**
     * Get transaction history
     */
    public List<Transaction> getTransactionHistory(int userId) throws NFCPayException {
        ValidationService.validatePositiveInteger(userId, "User ID");
        return transactionDAO.getTransactionsByUserId(userId);
    }
    
    /**
     * Get transaction by ID with authorization check
     */
    public Transaction getTransaction(int transactionId, int userId) throws NFCPayException {
        Transaction transaction = transactionDAO.getTransactionById(transactionId);
        if (transaction == null) {
            throw new ValidationException("Transaction not found");
        }
        if (transaction.getUserId() != userId) {
            throw new ValidationException("You are not authorized to view this transaction");
        }
        return transaction;
    }
    
    /**
     * Refund a payment transaction
     */
    public Transaction refundPayment(int transactionId, int userId, String reason) throws NFCPayException {
        Transaction originalTransaction = getTransaction(transactionId, userId);
        
        if (!"PAYMENT".equals(originalTransaction.getType())) {
            throw new ValidationException("Only payment transactions can be refunded");
        }
        if (!"SUCCESS".equals(originalTransaction.getStatus().toString())) {
            throw new ValidationException("Only completed transactions can be refunded");
        }
        
        // Check refund time limit (30 days)
        if (originalTransaction.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30))) {
            throw new ValidationException("Refund period has expired (30 days limit)");
        }
        
        // Create refund transaction
        Transaction refundTransaction = new Transaction(
            userId, originalTransaction.getCardId(), originalTransaction.getMerchantId(),
            originalTransaction.getAmount(), "REFUND", "SUCCESS",
            "Refund for transaction #" + transactionId + (reason != null ? " - " + reason : "")
        );
        refundTransaction.setProcessedAt(LocalDateTime.now());
        
        // Add amount back to wallet
        boolean added = walletDAO.addFunds(userId, originalTransaction.getAmount());
        if (!added) {
            throw new ValidationException("Failed to process refund");
        }
        
        boolean saved = transactionDAO.createTransaction(refundTransaction);
        if (!saved) {
            // Rollback
            walletDAO.deductFunds(userId, originalTransaction.getAmount());
            throw new ValidationException("Failed to save refund transaction");
        }
        
        return refundTransaction;
    }
    
    /**
     * Get spending analytics
     */
    public BigDecimal getMonthlySpending(int userId, int year, int month) throws NFCPayException {
        ValidationService.validatePositiveInteger(userId, "User ID");
        ValidationService.validateRange(year, "Year", 2020, 2030);
        ValidationService.validateRange(month, "Month", 1, 12);
        
        // Calculate monthly spending using date range
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return transactionDAO.getTotalSpent(userId, startDate, endDate);
    }
    
    /**
     * Check if payment amount is within limits
     */
    public void validatePaymentLimits(int userId, BigDecimal amount) throws NFCPayException {
        ValidationService.validateAmount(amount);
        
        // Single transaction limit
        BigDecimal singleTransactionLimit = new BigDecimal("1000");
        if (amount.compareTo(singleTransactionLimit) > 0) {
            throw new PaymentException("AMOUNT_LIMIT_EXCEEDED", "Single transaction limit exceeded", 
                String.format("Amount exceeds single transaction limit of $%.2f", singleTransactionLimit));
        }
        
        // Daily limit check
        BigDecimal dailySpent = transactionDAO.getDailySpent(userId);
        BigDecimal dailyLimit = new BigDecimal("5000");
        if (dailySpent.add(amount).compareTo(dailyLimit) > 0) {
            throw new PaymentException("DAILY_LIMIT_EXCEEDED", "Daily transaction limit exceeded", 
                String.format("Daily limit exceeded. Limit: $%.2f, Already spent: $%.2f", 
                    dailyLimit, dailySpent));
        }
    }
}