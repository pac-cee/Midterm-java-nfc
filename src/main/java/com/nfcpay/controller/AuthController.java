package com.nfcpay.controller;

import com.nfcpay.service.AuthService;
import com.nfcpay.model.User;
import com.nfcpay.exception.NFCPayException;

/**
 * Authentication Controller
 * Handles user authentication and profile management
 */
public class AuthController {
    private final AuthService authService;
    
    public AuthController() {
        this.authService = new AuthService();
    }
    
    public User registerUser(String fullName, String email, String password) throws NFCPayException {
        return authService.registerUser(fullName, email, password);
    }
    
    public User login(String email, String password) throws NFCPayException {
        return authService.login(email, password);
    }
    
    public void logout() {
        authService.logout();
    }
    
    public User getCurrentUser() {
        return authService.getCurrentUser();
    }
    
    public boolean isLoggedIn() {
        return authService.isLoggedIn();
    }
    
    public void changePassword(int userId, String currentPassword, String newPassword) throws NFCPayException {
        authService.changePassword(userId, currentPassword, newPassword);
    }
    
    public User updateProfile(int userId, String fullName, String email) throws NFCPayException {
        return authService.updateProfile(userId, fullName, email);
    }
    
    public void deactivateAccount(int userId) throws NFCPayException {
        authService.deactivateAccount(userId);
    }
}