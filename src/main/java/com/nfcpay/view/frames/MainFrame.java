package com.nfcpay.view.frames;

import com.nfcpay.controller.MainController;
import com.nfcpay.util.Session;
import com.nfcpay.util.UIUtils;
import com.nfcpay.util.ThemeManager;
import com.nfcpay.view.panels.*;
import com.nfcpay.view.dialogs.ProfileDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Main Application Frame - Dashboard with navigation
 */
public class MainFrame extends JFrame {
    private final MainController mainController;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Panels
    private DashboardPanel dashboardPanel;
    private CardPanel cardPanel;
    private WalletPanel walletPanel;
    private PaymentPanel paymentPanel;
    private TransactionPanel transactionPanel;
    
    public MainFrame(MainController mainController) {
        System.out.println("MainFrame constructor started");
        this.mainController = mainController;
        
        try {
            System.out.println("Initializing components...");
            initializeComponents();
            
            System.out.println("Setting up layout...");
            setupLayout();
            
            System.out.println("Setting up event handlers...");
            setupEventHandlers();
            
            System.out.println("Setting up frame...");
            setupFrame();
            
            System.out.println("Showing dashboard...");
            showDashboard();
            
            System.out.println("MainFrame constructor completed");
        } catch (Exception e) {
            System.err.println("Error in MainFrame constructor: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    private void initializeComponents() {
        System.out.println("Creating card layout...");
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Initialize panels one by one with error handling
        try {
            System.out.println("Creating dashboard panel...");
            dashboardPanel = new DashboardPanel(mainController);
            System.out.println("Dashboard panel created successfully");
            
            System.out.println("Creating card panel...");
            cardPanel = new CardPanel(mainController);
            System.out.println("Card panel created successfully");
            
            System.out.println("Creating wallet panel...");
            walletPanel = new WalletPanel(mainController);
            System.out.println("Wallet panel created successfully");
            
            System.out.println("Creating payment panel...");
            paymentPanel = new PaymentPanel(mainController);
            System.out.println("Payment panel created successfully");
            
            System.out.println("Creating transaction panel...");
            transactionPanel = new TransactionPanel(mainController);
            System.out.println("Transaction panel created successfully");
            
            System.out.println("All panels created successfully");
        } catch (Exception e) {
            System.err.println("Error creating panels: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Navigation Panel
        JPanel navPanel = createNavigationPanel();
        
        // Content Panel
        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(cardPanel, "CARDS");
        contentPanel.add(walletPanel, "WALLET");
        contentPanel.add(paymentPanel, "PAYMENT");
        contentPanel.add(transactionPanel, "TRANSACTIONS");
        
        add(navPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }
    

    
    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(248, 249, 250));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        navPanel.setPreferredSize(new Dimension(280, 0));
        
        // User Info
        JLabel userLabel = new JLabel("👋 Welcome, " + Session.getCurrentUser().getFullName());
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        navPanel.add(userLabel);
        navPanel.add(Box.createVerticalStrut(20));
        
        // Navigation Buttons
        JButton dashboardBtn = createNavButton("📊 Dashboard", "DASHBOARD");
        JButton cardsBtn = createNavButton("💳 My Cards", "CARDS");
        JButton walletBtn = createNavButton("💰 Wallet", "WALLET");
        JButton paymentBtn = createNavButton("💸 Make Payment", "PAYMENT");
        JButton transactionsBtn = createNavButton("📋 Transactions", "TRANSACTIONS");
        
        navPanel.add(dashboardBtn);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(cardsBtn);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(walletBtn);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(paymentBtn);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(transactionsBtn);
        
        // Push account buttons to bottom
        navPanel.add(Box.createVerticalGlue());
        navPanel.add(Box.createVerticalStrut(20));
        
        // Account Buttons
        JButton profileBtn = createAccountButton("👤 Profile");
        JButton themeBtn = createAccountButton(ThemeManager.isDarkMode() ? "☀️ Theme" : "🌙 Theme");
        JButton logoutBtn = createAccountButton("🚪 Logout");
        JButton aboutBtn = createAccountButton("ℹ️ About");
        
        profileBtn.addActionListener(e -> showProfile());
        themeBtn.addActionListener(e -> {
            ThemeManager.toggleTheme();
            themeBtn.setText(ThemeManager.isDarkMode() ? "☀️ Theme" : "🌙 Theme");
        });
        logoutBtn.addActionListener(this::handleLogout);
        aboutBtn.addActionListener(e -> showAbout());
        
        navPanel.add(profileBtn);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(themeBtn);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(aboutBtn);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(logoutBtn);
        
        return navPanel;
    }
    
    private JButton createNavButton(String text, String panelName) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(240, 60));
        button.setPreferredSize(new Dimension(240, 60));
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(e -> showPanel(panelName));
        return button;
    }
    
    private JButton createAccountButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(240, 55));
        button.setPreferredSize(new Dimension(240, 55));
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(108, 117, 125));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        return button;
    }
    
    private void setupEventHandlers() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                handleExit();
            }
        });
    }
    
    private void setupFrame() {
        setTitle("NFC Payment System - " + Session.getCurrentUser().getFullName());
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        UIUtils.centerWindow(this);
    }
    
    private void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
        
        // Refresh panel data when shown
        switch (panelName) {
            case "DASHBOARD":
                dashboardPanel.refreshData();
                break;
            case "CARDS":
                cardPanel.refreshData();
                break;
            case "WALLET":
                walletPanel.refreshData();
                break;
            case "PAYMENT":
                paymentPanel.refreshData();
                break;
            case "TRANSACTIONS":
                transactionPanel.refreshData();
                break;
        }
    }
    
    private void showDashboard() {
        showPanel("DASHBOARD");
    }
    
    private void showProfile() {
        new ProfileDialog(this, mainController).setVisible(true);
    }
    
    private void showAbout() {
        String message = "NFC Payment System v1.0\n\n" +
                        "Developed by: Pacifique Bakundukize\n" +
                        "Course: INSY 7312 - Java Programming\n" +
                        "Academic Year: 2025-2026";
        JOptionPane.showMessageDialog(this, message, "About", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void handleLogout(ActionEvent e) {
        if (UIUtils.showConfirmation(this, "Are you sure you want to logout?")) {
            Session.logout();
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
                dispose();
            });
        }
    }
    
    private void handleExit() {
        if (UIUtils.showConfirmation(this, "Are you sure you want to exit?")) {
            Session.logout();
            System.exit(0);
        }
    }
}