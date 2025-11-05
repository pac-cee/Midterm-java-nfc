package com.nfcpay.view.frames;

import com.nfcpay.controller.MainController;
import com.nfcpay.util.UIUtils;
import com.nfcpay.util.NotificationManager;
import com.nfcpay.view.components.CustomButton;
import com.nfcpay.exception.NFCPayException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Registration Frame - User registration interface
 */
public class RegisterFrame extends JFrame {
    private final MainController mainController;
    private final LoginFrame loginFrame;
    private JTextField fullNameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton;
    
    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        this.mainController = new MainController();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
    }
    
    private void initializeComponents() {
        fullNameField = new JTextField(25);
        fullNameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        fullNameField.setPreferredSize(new Dimension(300, 35));
        
        emailField = new JTextField(25);
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(300, 35));
        
        passwordField = new JPasswordField(25);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(300, 35));
        
        confirmPasswordField = new JPasswordField(25);
        confirmPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        confirmPasswordField.setPreferredSize(new Dimension(300, 35));
        registerButton = new CustomButton("✅ Create Account", CustomButton.ButtonStyle.SUCCESS);
        registerButton.setPreferredSize(new Dimension(300, 45));
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        backButton = new CustomButton("← Back to Login", CustomButton.ButtonStyle.SECONDARY);
        backButton.setPreferredSize(new Dimension(300, 45));
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 58, 64));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        JLabel titleLabel = new JLabel("🆕 Create Account", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        headerPanel.add(titleLabel);
        
        // Main Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Full Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        JLabel nameLabel = new JLabel("👤 Full Name:");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        mainPanel.add(nameLabel, gbc);
        gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(8, 0, 18, 0);
        mainPanel.add(fullNameField, gbc);
        
        // Email
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 0, 0);
        JLabel emailLabel = new JLabel("📧 Email:");
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        mainPanel.add(emailLabel, gbc);
        gbc.gridy = 3; gbc.insets = new Insets(8, 0, 18, 0);
        mainPanel.add(emailField, gbc);
        
        // Password
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 0, 0);
        JLabel passwordLabel = new JLabel("🔒 Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        mainPanel.add(passwordLabel, gbc);
        gbc.gridy = 5; gbc.insets = new Insets(8, 0, 18, 0);
        mainPanel.add(passwordField, gbc);
        
        // Confirm Password
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 0, 0);
        JLabel confirmLabel = new JLabel("🔓 Confirm Password:");
        confirmLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        mainPanel.add(confirmLabel, gbc);
        gbc.gridy = 7; gbc.insets = new Insets(8, 0, 25, 0);
        mainPanel.add(confirmPasswordField, gbc);
        
        // Register Button
        gbc.gridy = 8; gbc.insets = new Insets(0, 0, 15, 0); gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(registerButton, gbc);
        
        // Back Button
        gbc.gridy = 9; gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(backButton, gbc);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        registerButton.addActionListener(this::handleRegister);
        backButton.addActionListener(this::handleBack);
        getRootPane().setDefaultButton(registerButton);
    }
    
    private void setupFrame() {
        setTitle("NFC Payment System - Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 550);
        setResizable(false);
        UIUtils.centerWindow(this);
    }
    
    private void handleRegister(ActionEvent e) {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Client-side validation
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            NotificationManager.showError(this, "Please fill in all fields");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            NotificationManager.showError(this, "Passwords do not match");
            confirmPasswordField.setText("");
            return;
        }
        
        try {
            mainController.getAuthController().registerUser(fullName, email, password);
            NotificationManager.showSuccess(this, "Registration successful! Please login with your credentials.");
            
            // Return to login
            SwingUtilities.invokeLater(() -> {
                loginFrame.showFrame();
                dispose();
            });
            
        } catch (NFCPayException ex) {
            NotificationManager.showError(this, ex.getUserMessage());
        }
    }
    
    private void handleBack(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            loginFrame.showFrame();
            dispose();
        });
    }
}