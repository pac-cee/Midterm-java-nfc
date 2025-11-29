package com.nfcpay.view.frames;

import com.nfcpay.controller.MainController;
import com.nfcpay.util.Session;
import com.nfcpay.util.UIUtils;
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
        this.mainController = mainController;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
        showDashboard();
    }
    
    private void initializeComponents() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        dashboardPanel = new DashboardPanel(mainController);
        cardPanel = new CardPanel(mainController);
        walletPanel = new WalletPanel(mainController);
        paymentPanel = new PaymentPanel(mainController);
        transactionPanel = new TransactionPanel(mainController);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Professional Header
        JPanel headerPanel = createHeaderPanel();
        
        // Modern Sidebar Navigation
        JPanel sidebarPanel = createModernSidebar();
        
        // Content Panel
        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(cardPanel, "CARDS");
        contentPanel.add(walletPanel, "WALLET");
        contentPanel.add(paymentPanel, "PAYMENT");
        contentPanel.add(transactionPanel, "TRANSACTIONS");
        
        add(headerPanel, BorderLayout.NORTH);
        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
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
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIUtils.PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_SM, UIUtils.SPACING_MD, UIUtils.SPACING_SM, UIUtils.SPACING_MD));
        headerPanel.setPreferredSize(new Dimension(0, 60));
        
        // App title and logo
        JLabel titleLabel = new JLabel("🏢 NFC Payment System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        // User profile section
        JPanel userPanel = createUserProfilePanel();
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createUserProfilePanel() {
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIUtils.SPACING_SM, 0));
        userPanel.setBackground(UIUtils.PRIMARY);
        
        // User avatar and name
        JLabel avatarLabel = new JLabel("👤");
        avatarLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        avatarLabel.setForeground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(Session.getCurrentUser().getFullName());
        nameLabel.setFont(UIUtils.FONT_BODY);
        nameLabel.setForeground(Color.WHITE);
        
        // Profile dropdown button
        JButton profileBtn = new JButton("▼");
        profileBtn.setFont(UIUtils.FONT_SMALL);
        profileBtn.setForeground(Color.WHITE);
        profileBtn.setBackground(UIUtils.PRIMARY);
        profileBtn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        profileBtn.setFocusPainted(false);
        profileBtn.addActionListener(e -> showProfileMenu(profileBtn));
        
        userPanel.add(avatarLabel);
        userPanel.add(nameLabel);
        userPanel.add(profileBtn);
        
        return userPanel;
    }
    
    private JPanel createModernSidebar() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(UIUtils.getSurfaceColor());
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0xe2e8f0)),
            BorderFactory.createEmptyBorder(UIUtils.SPACING_MD, UIUtils.SPACING_SM, UIUtils.SPACING_MD, UIUtils.SPACING_SM)
        ));
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        
        // Navigation section
        JLabel navTitle = new JLabel("NAVIGATION");
        navTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        navTitle.setForeground(UIUtils.NEUTRAL);
        navTitle.setBorder(BorderFactory.createEmptyBorder(0, UIUtils.SPACING_SM, UIUtils.SPACING_SM, 0));
        
        sidebarPanel.add(navTitle);
        
        // Navigation buttons
        String[] navItems = {
            "📊 Dashboard|DASHBOARD",
            "💳 My Cards|CARDS", 
            "💰 Wallet|WALLET",
            "💸 Payment|PAYMENT",
            "📋 History|TRANSACTIONS"
        };
        
        for (String item : navItems) {
            String[] parts = item.split("\\|");
            JButton navBtn = createModernNavButton(parts[0], parts[1]);
            sidebarPanel.add(navBtn);
            sidebarPanel.add(Box.createVerticalStrut(UIUtils.SPACING_XS));
        }
        
        // Push settings to bottom
        sidebarPanel.add(Box.createVerticalGlue());
        
        // Settings section
        JLabel settingsTitle = new JLabel("SETTINGS");
        settingsTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        settingsTitle.setForeground(UIUtils.NEUTRAL);
        settingsTitle.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_MD, UIUtils.SPACING_SM, UIUtils.SPACING_SM, 0));
        
        sidebarPanel.add(settingsTitle);
        
        JButton aboutBtn = createModernNavButton("ℹ️ About", "ABOUT");
        aboutBtn.addActionListener(e -> showAbout());
        
        sidebarPanel.add(aboutBtn);
        
        return sidebarPanel;
    }
    
    private JButton createModernNavButton(String text, String panelName) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(220, 45));
        button.setPreferredSize(new Dimension(220, 45));
        button.setFont(UIUtils.FONT_BODY);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(UIUtils.getSurfaceColor());
        button.setForeground(UIUtils.getTextColor());
        button.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_SM, UIUtils.SPACING_SM, UIUtils.SPACING_SM, UIUtils.SPACING_SM));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(UIUtils.PRIMARY.brighter());
                button.setForeground(Color.WHITE);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(UIUtils.getSurfaceColor());
                button.setForeground(UIUtils.getTextColor());
            }
        });
        
        if (!panelName.equals("ABOUT")) {
            button.addActionListener(e -> showPanel(panelName));
        }
        
        return button;
    }
    
    private void showProfileMenu(JButton profileBtn) {
        JPopupMenu profileMenu = new JPopupMenu();
        profileMenu.setBackground(UIUtils.getSurfaceColor());
        
        JMenuItem profileItem = new JMenuItem("👤 View Profile");
        profileItem.setFont(UIUtils.FONT_BODY);
        profileItem.addActionListener(e -> showProfile());
        
        JMenuItem logoutItem = new JMenuItem("🚺 Logout");
        logoutItem.setFont(UIUtils.FONT_BODY);
        logoutItem.addActionListener(this::handleLogout);
        
        profileMenu.add(profileItem);
        profileMenu.addSeparator();
        profileMenu.add(logoutItem);
        
        profileMenu.show(profileBtn, 0, profileBtn.getHeight());
    }
    

}