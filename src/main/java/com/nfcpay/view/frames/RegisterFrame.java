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
        fullNameField = new JTextField(30);
        fullNameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        fullNameField.setPreferredSize(new Dimension(400, 50));
        fullNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        emailField = new JTextField(30);
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        emailField.setPreferredSize(new Dimension(400, 50));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        passwordField = new JPasswordField(30);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        passwordField.setPreferredSize(new Dimension(400, 50));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        confirmPasswordField = new JPasswordField(30);
        confirmPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        confirmPasswordField.setPreferredSize(new Dimension(400, 50));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        registerButton = new CustomButton("✅ Create Account", CustomButton.ButtonStyle.SUCCESS);
        registerButton.setPreferredSize(new Dimension(400, 55));
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        
        backButton = new CustomButton("← Back to Login", CustomButton.ButtonStyle.SECONDARY);
        backButton.setPreferredSize(new Dimension(400, 55));
        backButton.setFont(new Font("SansSerif", Font.BOLD, 18));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header Panel with gradient
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(46, 204, 113), 
                                                         getWidth(), 0, new Color(39, 174, 96));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 120));
        
        JLabel titleLabel = new JLabel("🆕 Create Your Account", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
        headerPanel.add(titleLabel);
        
        // Main content with card design
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));
        
        // Register card
        JPanel registerCard = new JPanel(new GridBagLayout());
        registerCard.setBackground(Color.WHITE);
        registerCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));
        registerCard.setPreferredSize(new Dimension(550, 550));
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Welcome text
        JLabel welcomeLabel = new JLabel("Join NFC Pay Today!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(52, 58, 64));
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 30, 0);
        registerCard.add(welcomeLabel, gbc);
        
        // Full Name
        JLabel nameLabel = new JLabel("👤 Full Name");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setForeground(new Color(73, 80, 87));
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 8, 0);
        registerCard.add(nameLabel, gbc);
        
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 20, 0);
        registerCard.add(fullNameField, gbc);
        
        // Email
        JLabel emailLabel = new JLabel("📧 Email Address");
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        emailLabel.setForeground(new Color(73, 80, 87));
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 8, 0);
        registerCard.add(emailLabel, gbc);
        
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 20, 0);
        registerCard.add(emailField, gbc);
        
        // Password
        JLabel passwordLabel = new JLabel("🔒 Password");
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        passwordLabel.setForeground(new Color(73, 80, 87));
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 8, 0);
        registerCard.add(passwordLabel, gbc);
        
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 20, 0);
        registerCard.add(passwordField, gbc);
        
        // Confirm Password
        JLabel confirmLabel = new JLabel("🔓 Confirm Password");
        confirmLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        confirmLabel.setForeground(new Color(73, 80, 87));
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 8, 0);
        registerCard.add(confirmLabel, gbc);
        
        gbc.gridy = 8; gbc.insets = new Insets(0, 0, 30, 0);
        registerCard.add(confirmPasswordField, gbc);
        
        // Register Button
        gbc.gridy = 9; gbc.insets = new Insets(0, 0, 15, 0);
        registerCard.add(registerButton, gbc);
        
        // Back Button
        gbc.gridy = 10; gbc.insets = new Insets(0, 0, 0, 0);
        registerCard.add(backButton, gbc);
        
        mainContainer.add(registerCard);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainContainer, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        registerButton.addActionListener(this::handleRegister);
        backButton.addActionListener(this::handleBack);
        getRootPane().setDefaultButton(registerButton);
    }
    
    private void setupFrame() {
        setTitle("NFC Payment System - Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
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