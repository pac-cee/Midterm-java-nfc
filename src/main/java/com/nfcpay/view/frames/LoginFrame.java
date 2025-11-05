package com.nfcpay.view.frames;

import com.nfcpay.controller.MainController;
import com.nfcpay.model.User;
import com.nfcpay.util.Session;
import com.nfcpay.util.UIUtils;
import com.nfcpay.util.ThemeManager;
import com.nfcpay.util.NotificationManager;
import com.nfcpay.exception.NFCPayException;
import com.nfcpay.view.components.CustomButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Login Frame - Modern login interface
 */
public class LoginFrame extends JFrame {
    private final MainController mainController;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    
    public LoginFrame() {
        this.mainController = new MainController();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
    }
    
    private void initializeComponents() {
        emailField = new JTextField(25);
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(300, 35));
        
        passwordField = new JPasswordField(25);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(300, 35));
        loginButton = new CustomButton("🔑 Login", CustomButton.ButtonStyle.PRIMARY);
        loginButton.setPreferredSize(new Dimension(300, 45));
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        registerButton = new CustomButton("🆕 Register", CustomButton.ButtonStyle.SUCCESS);
        registerButton.setPreferredSize(new Dimension(300, 45));
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        // Theme toggle button
        JButton themeButton = new JButton("🌙");
        themeButton.setPreferredSize(new Dimension(40, 40));
        themeButton.addActionListener(e -> {
            ThemeManager.toggleTheme();
            themeButton.setText(ThemeManager.isDarkMode() ? "☀️" : "🌙");
        });
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 58, 64));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        JLabel titleLabel = new JLabel("NFC Payment System 💳", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        headerPanel.add(titleLabel);
        
        // Add theme toggle to header
        JButton themeButton = new JButton("🌙");
        themeButton.setPreferredSize(new Dimension(40, 40));
        themeButton.addActionListener(e -> {
            ThemeManager.toggleTheme();
            themeButton.setText(ThemeManager.isDarkMode() ? "☀️" : "🌙");
        });
        
        JPanel headerContent = new JPanel(new BorderLayout());
        headerContent.add(titleLabel, BorderLayout.CENTER);
        headerContent.add(themeButton, BorderLayout.EAST);
        headerContent.setOpaque(false);
        headerPanel.removeAll();
        headerPanel.add(headerContent);
        
        // Main Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Email
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        JLabel emailLabel = new JLabel("📧 Email:");
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        mainPanel.add(emailLabel, gbc);
        gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(8, 0, 20, 0);
        mainPanel.add(emailField, gbc);
        
        // Password
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 0, 0);
        JLabel passwordLabel = new JLabel("🔒 Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        mainPanel.add(passwordLabel, gbc);
        gbc.gridy = 3; gbc.insets = new Insets(8, 0, 30, 0);
        mainPanel.add(passwordField, gbc);
        
        // Login Button
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 10, 0); gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(loginButton, gbc);
        
        // Register Button
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(registerButton, gbc);
        
        // "New User?" label
        JLabel newUserLabel = new JLabel("Don't have an account? Click Register above", SwingConstants.CENTER);
        newUserLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        newUserLabel.setForeground(Color.GRAY);
        gbc.gridy = 6; gbc.insets = new Insets(10, 0, 0, 0);
        mainPanel.add(newUserLabel, gbc);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        loginButton.addActionListener(e -> {
            System.out.println("Login button clicked!");
            handleLogin(e);
        });
        registerButton.addActionListener(this::handleRegister);
        
        // Enter key support
        getRootPane().setDefaultButton(loginButton);
    }
    
    private void setupFrame() {
        setTitle("NFC Payment System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 500);
        setResizable(false);
        UIUtils.centerWindow(this);
    }
    
    private void handleLogin(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        System.out.println("Login attempt: " + email); // Debug
        
        if (email.isEmpty() || password.isEmpty()) {
            NotificationManager.showError(this, "Please enter both email and password");
            return;
        }
        
        try {
            // Show loading
            loginButton.setText("Logging in...");
            loginButton.setEnabled(false);
            
            User user = mainController.getAuthController().login(email, password);
            Session.setCurrentUser(user);
            
            NotificationManager.showSuccess(this, "Login successful! Welcome " + user.getFullName());
            
            // Open main application
            System.out.println("Opening MainFrame for user: " + user.getFullName());
            SwingUtilities.invokeLater(() -> {
                try {
                    MainFrame mainFrame = new MainFrame(mainController);
                    mainFrame.setVisible(true);
                    dispose();
                    System.out.println("MainFrame opened successfully");
                } catch (Exception ex) {
                    System.err.println("Error opening MainFrame: " + ex.getMessage());
                    ex.printStackTrace();
                    // Fallback to simple frame
                    SimpleMainFrame simpleFrame = new SimpleMainFrame(user);
                    simpleFrame.setVisible(true);
                    dispose();
                }
            });
            
        } catch (Exception ex) {
            System.err.println("Login error: " + ex.getMessage()); // Debug
            ex.printStackTrace();
            NotificationManager.showError(this, "Login failed: " + ex.getMessage());
            passwordField.setText("");
        } finally {
            SwingUtilities.invokeLater(() -> {
                loginButton.setText("🔑 Login");
                loginButton.setEnabled(true);
            });
        }
    }
    
    private void handleRegister(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            new RegisterFrame(this).setVisible(true);
            setVisible(false);
        });
    }
    
    public void showFrame() {
        emailField.setText("");
        passwordField.setText("");
        setVisible(true);
    }
}