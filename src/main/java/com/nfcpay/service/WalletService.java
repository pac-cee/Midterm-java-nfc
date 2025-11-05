package com.nfcpay.service;

import com.nfcpay.dao.WalletDAO;
import com.nfcpay.dao.UserDAO;
import com.nfcpay.model.Wallet;
import com.nfcpay.model.User;
import com.nfcpay.model.enums.Currency;
import com.nfcpay.exception.ValidationException;
import com.nfcpay.exception.NFCPayException;
import java.math.BigDecimal;

/**
 * Wallet Management Service
 */
public class WalletService {
    private final WalletDAO walletDAO;
    private final UserDAO userDAO;
    
    public WalletService() {
        this.walletDAO = new WalletDAO();
        this.userDAO = new UserDAO();
    }
    
    /**
     * Get user's wallet
     */
    public Wallet getWallet(int userId) throws NFCPayException {
        ValidationService.validatePositiveInteger(userId, "User ID");
        
        User user = userDAO.getUserById(userId);
        if (user == null || !user.isActive()) {
            throw new ValidationException("User account is not active");
        }
        
        Wallet wallet = walletDAO.getWalletByUserId(userId);
        if (wallet == null) {
            throw new ValidationException("Wallet not found for user");
        }
        
        return wallet;
    }
    
    /**
     * Add funds to wallet
     */
    public void addFunds(int userId, BigDecimal amount, String source) throws NFCPayException {
        ValidationService.validatePositiveInteger(userId, "User ID");
        ValidationService.validateAmount(amount);
        
        // Validate maximum single deposit
        BigDecimal maxDeposit = new BigDecimal("2000");
        if (amount.compareTo(maxDeposit) > 0) {
            throw new ValidationException(String.format("Maximum deposit amount is $%.2f per transaction", maxDeposit));
        }
        
        Wallet wallet = getWallet(userId);
        
        // Validate maximum wallet balance
        BigDecimal maxBalance = new BigDecimal("10000");
        BigDecimal newBalance = wallet.getBalance().add(amount);
        if (newBalance.compareTo(maxBalance) > 0) {
            throw new ValidationException(String.format("Wallet balance cannot exceed $%.2f", maxBalance));
        }
        
        // Add funds using DAO method
        boolean success = walletDAO.addFunds(userId, amount);
        if (!success) {
            throw new ValidationException("Failed to add funds to wallet");
        }
    }
    
    /**
     * Withdraw funds from wallet
     */
    public void withdrawFunds(int userId, BigDecimal amount, String destination) throws NFCPayException {
        ValidationService.validatePositiveInteger(userId, "User ID");
        ValidationService.validateAmount(amount);
        
        Wallet wallet = getWallet(userId);
        
        // Check sufficient balance
        if (!walletDAO.hasSufficientBalance(userId, amount)) {
            throw new ValidationException(String.format("Insufficient funds. Available: $%.2f, Requested: $%.2f", 
                wallet.getBalance(), amount));
        }
        
        // Validate minimum withdrawal
        BigDecimal minWithdrawal = new BigDecimal("10");
        if (amount.compareTo(minWithdrawal) < 0) {
            throw new ValidationException(String.format("Minimum withdrawal amount is $%.2f", minWithdrawal));
        }
        
        // Validate maximum single withdrawal
        BigDecimal maxWithdrawal = new BigDecimal("1000");
        if (amount.compareTo(maxWithdrawal) > 0) {
            throw new ValidationException(String.format("Maximum withdrawal amount is $%.2f per transaction", maxWithdrawal));
        }
        
        // Deduct funds using DAO method
        boolean success = walletDAO.deductFunds(userId, amount);
        if (!success) {
            throw new ValidationException("Failed to withdraw funds from wallet");
        }
    }
    
    /**
     * Transfer funds between wallets
     */
    public void transferFunds(int fromUserId, int toUserId, BigDecimal amount, String description) throws NFCPayException {
        ValidationService.validatePositiveInteger(fromUserId, "From User ID");
        ValidationService.validatePositiveInteger(toUserId, "To User ID");
        ValidationService.validateAmount(amount);
        
        if (fromUserId == toUserId) {
            throw new ValidationException("Cannot transfer funds to the same wallet");
        }
        
        // Validate maximum transfer amount
        BigDecimal maxTransfer = new BigDecimal("500");
        if (amount.compareTo(maxTransfer) > 0) {
            throw new ValidationException(String.format("Maximum transfer amount is $%.2f", maxTransfer));
        }
        
        Wallet fromWallet = getWallet(fromUserId);
        Wallet toWallet = getWallet(toUserId);
        
        // Check sufficient balance
        if (!walletDAO.hasSufficientBalance(fromUserId, amount)) {
            throw new ValidationException("Insufficient funds for transfer");
        }
        
        // Check recipient wallet limit
        BigDecimal maxBalance = new BigDecimal("10000");
        if (toWallet.getBalance().add(amount).compareTo(maxBalance) > 0) {
            throw new ValidationException("Transfer would exceed recipient's wallet limit");
        }
        
        // Perform transfer
        boolean deducted = walletDAO.deductFunds(fromUserId, amount);
        if (!deducted) {
            throw new ValidationException("Failed to deduct funds from sender wallet");
        }
        
        boolean added = walletDAO.addFunds(toUserId, amount);
        if (!added) {
            // Rollback - add funds back to sender
            walletDAO.addFunds(fromUserId, amount);
            throw new ValidationException("Failed to add funds to recipient wallet");
        }
    }
    
    /**
     * Check if wallet has sufficient funds
     */
    public boolean hasSufficientFunds(int userId, BigDecimal amount) throws NFCPayException {
        return walletDAO.hasSufficientBalance(userId, amount);
    }
    
    /**
     * Get wallet balance
     */
    public BigDecimal getBalance(int userId) throws NFCPayException {
        return walletDAO.getBalance(userId);
    }
    
    /**
     * Update wallet currency
     */
    public void updateCurrency(int userId, Currency currency) throws NFCPayException {
        ValidationService.validateNotNull(currency, "Currency");
        
        Wallet wallet = getWallet(userId);
        
        if (wallet.getCurrency() == currency) {
            throw new ValidationException("Wallet is already in " + currency + " currency");
        }
        
        boolean updated = walletDAO.updateCurrency(userId, currency);
        if (!updated) {
            throw new ValidationException("Failed to update wallet currency");
        }
    }
    
    /**
     * Validate wallet for transaction
     */
    public void validateWalletForTransaction(int userId, BigDecimal amount) throws NFCPayException {
        if (!walletDAO.hasSufficientBalance(userId, amount)) {
            BigDecimal balance = walletDAO.getBalance(userId);
            throw new ValidationException(String.format("Insufficient wallet balance. Available: $%.2f, Required: $%.2f", 
                balance, amount));
        }
    }
}