package com.nfcpay.view.frames;

import com.nfcpay.model.User;
import com.nfcpay.util.Session;

import javax.swing.*;
import java.awt.*;

public class SimpleMainFrame extends JFrame {
    
    public SimpleMainFrame(User user) {
        setTitle("NFC Payment System - Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        JLabel titleLabel = new JLabel("💳 NFC Payment System", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        
        // Content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        
        JLabel welcomeLabel = new JLabel("👋 Welcome, " + user.getFullName() + "!");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        welcomeLabel.setForeground(new Color(52, 73, 94));
        
        JLabel statusLabel = new JLabel("🎉 Login Successful! Dashboard is ready.");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        statusLabel.setForeground(new Color(39, 174, 96));
        
        JButton logoutButton = new JButton("🚪 Logout");
        logoutButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        logoutButton.setPreferredSize(new Dimension(150, 50));
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        
        logoutButton.addActionListener(e -> {
            Session.logout();
            dispose();
            new LoginFrame().setVisible(true);
        });
        
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(welcomeLabel, gbc);
        gbc.gridy = 1;
        contentPanel.add(statusLabel, gbc);
        gbc.gridy = 2;
        contentPanel.add(logoutButton, gbc);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
}