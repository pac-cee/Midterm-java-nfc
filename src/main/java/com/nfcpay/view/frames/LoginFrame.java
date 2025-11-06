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
        
        loginButton = new CustomButton("🔑 Login", CustomButton.ButtonStyle.PRIMARY);
        loginButton.setPreferredSize(new Dimension(400, 55));
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        
        registerButton = new CustomButton("🆕 Register", CustomButton.ButtonStyle.SUCCESS);
        registerButton.setPreferredSize(new Dimension(400, 55));
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 18));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header Panel with gradient
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(41, 128, 185), 
                                                         getWidth(), 0, new Color(52, 152, 219));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 120));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("💳 NFC Payment System", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        
        JButton themeButton = new JButton("🌙");
        themeButton.setPreferredSize(new Dimension(50, 50));
        themeButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
        themeButton.setBackground(new Color(255, 255, 255, 100));
        themeButton.setBorder(BorderFactory.createEmptyBorder());
        themeButton.setFocusPainted(false);
        themeButton.addActionListener(e -> {
            ThemeManager.toggleTheme();
            themeButton.setText(ThemeManager.isDarkMode() ? "☀️" : "🌙");
        });
        
        JPanel headerContent = new JPanel(new BorderLayout());
        headerContent.add(titleLabel, BorderLayout.CENTER);
        headerContent.add(themeButton, BorderLayout.EAST);
        headerContent.setOpaque(false);
        headerContent.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        headerPanel.add(headerContent);
        
        // Main content with card design
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));
        
        // Login card
        JPanel loginCard = new JPanel(new GridBagLayout());
        loginCard.setBackground(Color.WHITE);
        loginCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(50, 50, 50, 50)
        ));
        loginCard.setPreferredSize(new Dimension(500, 450));
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Welcome text
        JLabel welcomeLabel = new JLabel("Welcome Back!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(52, 58, 64));
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 40, 0);
        loginCard.add(welcomeLabel, gbc);
        
        // Email
        JLabel emailLabel = new JLabel("📧 Email Address");
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        emailLabel.setForeground(new Color(73, 80, 87));
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 8, 0);
        loginCard.add(emailLabel, gbc);
        
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 25, 0);
        loginCard.add(emailField, gbc);
        
        // Password
        JLabel passwordLabel = new JLabel("🔒 Password");
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        passwordLabel.setForeground(new Color(73, 80, 87));
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 8, 0);
        loginCard.add(passwordLabel, gbc);
        
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 35, 0);
        loginCard.add(passwordField, gbc);
        
        // Login Button
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 15, 0);
        loginCard.add(loginButton, gbc);
        
        // Register Button
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 20, 0);
        loginCard.add(registerButton, gbc);
        
        // Help text
        JLabel helpLabel = new JLabel("Don't have an account? Click Register above", SwingConstants.CENTER);
        helpLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        helpLabel.setForeground(new Color(108, 117, 125));
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 0, 0);
        loginCard.add(helpLabel, gbc);
        
        mainContainer.add(loginCard);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainContainer, BorderLayout.CENTER);
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
        setSize(1000, 700);
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
            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame(mainController);
                mainFrame.setVisible(true);
                dispose();
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