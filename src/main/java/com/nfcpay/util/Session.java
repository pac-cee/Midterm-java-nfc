package com.nfcpay.util;

import com.nfcpay.model.User;
import java.time.LocalDateTime;

/**
 * Session Management Utility
 */
public class Session {
    private static User currentUser;
    private static LocalDateTime loginTime;
    
    public static void setCurrentUser(User user) {
        currentUser = user;
        loginTime = LocalDateTime.now();
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public static void logout() {
        currentUser = null;
        loginTime = null;
    }
    
    public static LocalDateTime getLoginTime() {
        return loginTime;
    }
}