package com.nfcpay.util;

import javax.swing.*;
import java.awt.*;

public class NotificationManager {
    
    public static void showSuccess(Component parent, String message) {
        showMessage(parent, "Success", message, JOptionPane.INFORMATION_MESSAGE, "✅");
    }
    
    public static void showError(Component parent, String message) {
        showMessage(parent, "Error", message, JOptionPane.ERROR_MESSAGE, "❌");
    }
    
    public static void showWarning(Component parent, String message) {
        showMessage(parent, "Warning", message, JOptionPane.WARNING_MESSAGE, "⚠️");
    }
    
    public static void showInfo(Component parent, String message) {
        showMessage(parent, "Information", message, JOptionPane.INFORMATION_MESSAGE, "ℹ️");
    }
    
    public static boolean showConfirmation(Component parent, String message) {
        return showConfirmation(parent, "Confirm Action", message);
    }
    
    public static boolean showConfirmation(Component parent, String title, String message) {
        int result = JOptionPane.showConfirmDialog(
            parent,
            "⚠️ " + message,
            title,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
    
    public static String showInput(Component parent, String message, String defaultValue) {
        return (String) JOptionPane.showInputDialog(
            parent,
            message,
            "Input Required",
            JOptionPane.QUESTION_MESSAGE,
            null,
            null,
            defaultValue
        );
    }
    
    private static void showMessage(Component parent, String title, String message, 
                                  int messageType, String icon) {
        // Create custom message with icon
        String formattedMessage = icon + " " + message;
        
        JOptionPane.showMessageDialog(
            parent,
            formattedMessage,
            title,
            messageType
        );
    }
    
    public static void showValidationError(Component parent, String fieldName, String error) {
        showError(parent, "Validation Error in " + fieldName + ":\n" + error);
    }
    
    public static void showOperationSuccess(Component parent, String operation) {
        showSuccess(parent, operation + " completed successfully!");
    }
    
    public static void showOperationError(Component parent, String operation, String error) {
        showError(parent, "Failed to " + operation + ":\n" + error);
    }
}