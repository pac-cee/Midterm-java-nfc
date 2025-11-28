package com.nfcpay;

import com.nfcpay.view.frames.LoginFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

/**
 * Main Application Entry Point
 */
public class Main {
    public static void main(String[] args) {
        // Set FlatLaf Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf theme: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Start application on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Failed to start application: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}