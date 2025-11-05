package com.nfcpay.service;

import com.nfcpay.dao.UserDAO;
import com.nfcpay.dao.WalletDAO;
import com.nfcpay.model.User;
import com.nfcpay.model.Wallet;
import com.nfcpay.model.enums.Currency;
import com.nfcpay.exception.ValidationException;
import com.nfcpay.exception.NFCPayException;
import org.mindrot.jbcrypt.BCrypt;
import java.math.BigDecimal;

/**
 * Authentication and User Management Service
 */
public class AuthService {
    private final UserDAO userDAO;
    private final WalletDAO walletDAO;
    private User currentUser;
    
    public AuthService() {
        this.userDAO = new UserDAO();
        this.walletDAO = new WalletDAO();
    }
    
    /**
     * Register new user with validation
     */
    public User registerUser(String fullName, String email, String password) throws NFCPayException {
        // Validate inputs
        ValidationService.validateStringLength(fullName, "Full name", 2, 100);
        ValidationService.validateEmail(email);
        ValidationService.validatePassword(password);
        
        // Check if email already exists
        if (userDAO.getUserByEmail(email) != null) {
            throw new ValidationException("Email already registered");
        }
        
        // Hash password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        
        // Create user
        User user = new User(fullName, email, hashedPassword, null);
        boolean userCreated = userDAO.createUser(user);
        
        if (!userCreated) {
            throw new NFCPayException("USER_CREATION_FAILED", "Failed to create user", "Registration failed. Please try again.");
        }
        
        // Create default wallet
        Wallet wallet = new Wallet(user.getUserId(), BigDecimal.ZERO, Currency.USD);
        walletDAO.createWallet(wallet);
        
        return user;
    }
    
    /**
     * Authenticate user login
     */
    public User login(String email, String password) throws NFCPayException {
        ValidationService.validateEmail(email);
        ValidationService.validateNotNull(password, "Password");
        
        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            throw new ValidationException("Invalid email or password");
        }
        
        if (!user.isActive()) {
            throw new ValidationException("Account is deactivated. Please contact support.");
        }
        
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            throw new ValidationException("Invalid email or password");
        }
        
        // Update last login
        userDAO.updateLastLogin(user.getUserId());
        this.currentUser = user;
        
        return user;
    }
    
    /**
     * Change user password
     */
    public void changePassword(int userId, String currentPassword, String newPassword) throws NFCPayException {
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new ValidationException("User not found");
        }
        
        if (!BCrypt.checkpw(currentPassword, user.getPasswordHash())) {
            throw new ValidationException("Current password is incorrect");
        }
        
        ValidationService.validatePassword(newPassword);
        
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        userDAO.updatePassword(userId, hashedPassword);
    }
    
    /**
     * Update user profile
     */
    public User updateProfile(int userId, String fullName, String email) throws NFCPayException {
        ValidationService.validateStringLength(fullName, "Full name", 2, 100);
        ValidationService.validateEmail(email);
        
        User existingUser = userDAO.getUserByEmail(email);
        if (existingUser != null && existingUser.getUserId() != userId) {
            throw new ValidationException("Email already in use by another account");
        }
        
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new ValidationException("User not found");
        }
        
        user.setFullName(fullName);
        user.setEmail(email);
        
        boolean updated = userDAO.updateUser(user);
        if (!updated) {
            throw new ValidationException("Failed to update user profile");
        }
        
        return user;
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        this.currentUser = null;
    }
    
    /**
     * Get current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Deactivate user account
     */
    public void deactivateAccount(int userId) throws NFCPayException {
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new ValidationException("User not found");
        }
        
        user.setActive(false);
        boolean updated = userDAO.updateUser(user);
        if (!updated) {
            throw new ValidationException("Failed to deactivate account");
        }
        
        if (currentUser != null && currentUser.getUserId() == userId) {
            logout();
        }
    }
}