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
        // Any initialization logic can go here
        System.out.println("NFC Payment System initialized successfully");
    }
    
    // Application shutdown
    public void shutdownApplication() {
        if (isUserLoggedIn()) {
            logout();
        }
        System.out.println("NFC Payment System shutdown");
    }
}