package com.nfcpay.controller;

import com.nfcpay.model.User;
import com.nfcpay.exception.NFCPayException;

/**
 * Main Application Controller
 * Coordinates between different controllers and manages application state
 */
public class MainController {
    private final AuthController authController;
    private final CardController cardController;
    private final WalletController walletController;
    private final PaymentController paymentController;
    
    public MainController() {
        this.authController = new AuthController();
        this.cardController = new CardController();
        this.walletController = new WalletController();
        this.paymentController = new PaymentController();
    }
    
    // Getter methods for accessing individual controllers
    public AuthController getAuthController() {
        return authController;
    }
    
    public CardController getCardController() {
        return cardController;
    }
    
    public WalletController getWalletController() {
        return walletController;
    }
    
    public PaymentController getPaymentController() {
        return paymentController;
    }
    

    
    // Common application operations
    public boolean isUserLoggedIn() {
        return authController.isLoggedIn();
    }
    
    public User getCurrentUser() {
        return authController.getCurrentUser();
    }
    
    public void logout() {
        authController.logout();
    }
    
    // Application initialization
    public void initializeApplication() throws NFCPayException {
        try {
            // 1. Initialize database connection
            com.nfcpay.dao.DatabaseConnection.getInstance();
            
            // 2. Clear any existing session
            com.nfcpay.util.Session.logout();
            
            // 3. Validate service layer connectivity
            validateServices();
            
            System.out.println("✅ NFC Payment System initialized successfully");
        } catch (Exception e) {
            throw new NFCPayException("INIT_FAILED", "Application initialization failed", e.getMessage());
        }
    }
    
    private void validateServices() throws NFCPayException {
        // Test database connectivity through services
        try {
            authController.isLoggedIn(); // Simple validation call
        } catch (Exception e) {
            throw new NFCPayException("SERVICE_VALIDATION_FAILED", "Service layer validation failed", e.getMessage());
        }
    }
    
    // Application shutdown
    public void shutdownApplication() {
        try {
            if (isUserLoggedIn()) {
                logout();
            }
            
            // Clear session
            com.nfcpay.util.Session.logout();
            
            System.out.println("✅ NFC Payment System shutdown completed");
        } catch (Exception e) {
            System.err.println("⚠️ Warning during shutdown: " + e.getMessage());
        }
    }
}