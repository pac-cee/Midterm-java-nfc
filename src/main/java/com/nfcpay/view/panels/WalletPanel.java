package com.nfcpay.view.panels;

import com.nfcpay.controller.MainController;
import com.nfcpay.model.Wallet;
import com.nfcpay.util.Session;
import com.nfcpay.util.UIUtils;
import com.nfcpay.view.components.CustomButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;

/**
 * Wallet Management Panel
 */
public class WalletPanel extends JPanel {
    private final MainController mainController;
    private JLabel balanceLabel;
    private JLabel currencyLabel;
    private JTextField amountField;
    private JButton addFundsButton;
    private JButton withdrawButton;
    
    public WalletPanel(MainController mainController) {
        this.mainController = mainController;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        balanceLabel = new JLabel("$0.00");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 36));
        balanceLabel.setForeground(new Color(40, 167, 69));
        
        currencyLabel = new JLabel("USD");
        currencyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        amountField = new JTextField(15);
        addFundsButton = CustomButton.createSuccessButton("Add Funds");
        withdrawButton = CustomButton.createDangerButton("Withdraw");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Header with gradient
        JPanel headerPanel = new JPanel(new BorderLayout()) {
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
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        JLabel titleLabel = new JLabel("💰 My Wallet");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Balance Card
        JPanel balanceCard = new JPanel(new BorderLayout());
        balanceCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        balanceCard.setBackground(Color.WHITE);
        
        JLabel balanceTitle = new JLabel("Current Balance");
        balanceTitle.setFont(new Font("Arial", Font.PLAIN, 16));
        balanceTitle.setForeground(Color.GRAY);
        
        JPanel balanceInfo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        balanceInfo.add(balanceLabel);
        balanceInfo.add(currencyLabel);
        
        balanceCard.add(balanceTitle, BorderLayout.NORTH);
        balanceCard.add(balanceInfo, BorderLayout.CENTER);
        
        // Operations Panel
        JPanel operationsPanel = new JPanel(new GridBagLayout());
        operationsPanel.setBorder(BorderFactory.createTitledBorder("Wallet Operations"));
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        operationsPanel.add(new JLabel("Amount:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        operationsPanel.add(amountField, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addFundsButton);
        buttonPanel.add(withdrawButton);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        operationsPanel.add(buttonPanel, gbc);
        
        // Layout
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        centerPanel.add(balanceCard, BorderLayout.NORTH);
        centerPanel.add(operationsPanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        addFundsButton.addActionListener(this::handleAddFunds);
        withdrawButton.addActionListener(this::handleWithdraw);
    }
    
    private void handleAddFunds(ActionEvent e) {
        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            UIUtils.showError(this, "Please enter an amount");
            return;
        }
        
        try {
            BigDecimal amount = new BigDecimal(amountText);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                UIUtils.showError(this, "Amount must be greater than zero");
                return;
            }
            
            if (UIUtils.showConfirmation(this, 
                String.format("Add $%.2f to your wallet?", amount))) {
                
                mainController.getWalletController().addFunds(
                    Session.getCurrentUser().getUserId(), amount, "Manual deposit");
                
                UIUtils.showSuccess(this, "Funds added successfully!");
                amountField.setText("");
                refreshData();
            }
            
        } catch (NumberFormatException ex) {
            UIUtils.showError(this, "Please enter a valid amount");
        } catch (Exception ex) {
            UIUtils.showError(this, "Failed to add funds: " + ex.getMessage());
        }
    }
    
    private void handleWithdraw(ActionEvent e) {
        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            UIUtils.showError(this, "Please enter an amount");
            return;
        }
        
        try {
            BigDecimal amount = new BigDecimal(amountText);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                UIUtils.showError(this, "Amount must be greater than zero");
                return;
            }
            
            if (UIUtils.showConfirmation(this, 
                String.format("Withdraw $%.2f from your wallet?", amount))) {
                
                mainController.getWalletController().withdrawFunds(
                    Session.getCurrentUser().getUserId(), amount, "Manual withdrawal");
                
                UIUtils.showSuccess(this, "Withdrawal successful!");
                amountField.setText("");
                refreshData();
            }
            
        } catch (NumberFormatException ex) {
            UIUtils.showError(this, "Please enter a valid amount");
        } catch (Exception ex) {
            UIUtils.showError(this, "Failed to withdraw funds: " + ex.getMessage());
        }
    }
    
    public void refreshData() {
        try {
            Wallet wallet = mainController.getWalletController().getWallet(Session.getCurrentUser().getUserId());
            balanceLabel.setText(String.format("$%.2f", wallet.getBalance()));
            currencyLabel.setText(wallet.getCurrency().toString());
            
        } catch (Exception e) {
            UIUtils.showError(this, "Failed to load wallet data: " + e.getMessage());
        }
    }
}