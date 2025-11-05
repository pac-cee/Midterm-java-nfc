package com.nfcpay.controller;

import com.nfcpay.service.PaymentService;
import com.nfcpay.model.Transaction;
import com.nfcpay.model.enums.TransactionType;
import com.nfcpay.exception.NFCPayException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Payment Processing Controller
 * Handles payment operations through PaymentService
 */
public class PaymentController {
    private final PaymentService paymentService;
    
    public PaymentController() {
        this.paymentService = new PaymentService();
    }
    
    public Transaction processPayment(int userId, int cardId, int merchantId, BigDecimal amount, String description) throws NFCPayException {
        return paymentService.processPayment(userId, cardId, merchantId, amount, description);
    }
    
    public List<Transaction> getTransactionHistory(int userId) throws NFCPayException {
        return paymentService.getTransactionHistory(userId);
    }
    
    public Transaction getTransaction(int transactionId, int userId) throws NFCPayException {
        return paymentService.getTransaction(transactionId, userId);
    }
    
    public Transaction refundPayment(int transactionId, int userId, String reason) throws NFCPayException {
        return paymentService.refundPayment(transactionId, userId, reason);
    }
    
    public BigDecimal getMonthlySpending(int userId, int year, int month) throws NFCPayException {
        return paymentService.getMonthlySpending(userId, year, month);
    }
    
    public void validatePaymentLimits(int userId, BigDecimal amount) throws NFCPayException {
        paymentService.validatePaymentLimits(userId, amount);
    }
}