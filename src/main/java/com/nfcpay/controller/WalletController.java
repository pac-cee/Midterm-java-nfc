package com.nfcpay.controller;

import com.nfcpay.service.WalletService;
import com.nfcpay.model.Wallet;
import com.nfcpay.model.enums.Currency;
import com.nfcpay.exception.NFCPayException;
import java.math.BigDecimal;

/**
 * Wallet Management Controller
 * Handles wallet operations through WalletService
 */
public class WalletController {
    private final WalletService walletService;
    
    public WalletController() {
        this.walletService = new WalletService();
    }
    
    public Wallet getWallet(int userId) throws NFCPayException {
        return walletService.getWallet(userId);
    }
    
    public void addFunds(int userId, BigDecimal amount, String source) throws NFCPayException {
        walletService.addFunds(userId, amount, source);
    }
    
    public void withdrawFunds(int userId, BigDecimal amount, String destination) throws NFCPayException {
        walletService.withdrawFunds(userId, amount, destination);
    }
    
    public void transferFunds(int fromUserId, int toUserId, BigDecimal amount, String description) throws NFCPayException {
        walletService.transferFunds(fromUserId, toUserId, amount, description);
    }
    
    public boolean hasSufficientFunds(int userId, BigDecimal amount) throws NFCPayException {
        return walletService.hasSufficientFunds(userId, amount);
    }
    
    public BigDecimal getBalance(int userId) throws NFCPayException {
        return walletService.getBalance(userId);
    }
    
    public void updateCurrency(int userId, Currency currency) throws NFCPayException {
        walletService.updateCurrency(userId, currency);
    }
    
    public void validateWalletForTransaction(int userId, BigDecimal amount) throws NFCPayException {
        walletService.validateWalletForTransaction(userId, amount);
    }
}