package com.nfcpay.view.panels;

import com.nfcpay.controller.MainController;
import com.nfcpay.model.Wallet;
import com.nfcpay.model.Card;
import com.nfcpay.util.Session;
import com.nfcpay.util.UIUtils;
import com.nfcpay.view.components.CustomButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.List;

/**
 * Wallet Management Panel
 */
public class WalletPanel extends JPanel {
    private final MainController mainController;
    private JLabel balanceLabel;
    private JLabel currencyLabel;
    private JTextField amountField;
    private JComboBox<Card> cardComboBox;
    private JButton addFundsButton;
    private JButton withdrawButton;
    private JPanel activityList;
    
    public WalletPanel(MainController mainController) {
        this.mainController = mainController;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        balanceLabel = new JLabel("$0.00");
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        balanceLabel.setForeground(Color.WHITE);
        
        currencyLabel = new JLabel("USD");
        currencyLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        currencyLabel.setForeground(Color.WHITE);
        
        amountField = new JTextField(15);
        amountField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        amountField.setPreferredSize(new Dimension(200, 40));
        amountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        cardComboBox = new JComboBox<>();
        cardComboBox.setRenderer(new CardComboBoxRenderer());
        cardComboBox.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cardComboBox.setPreferredSize(new Dimension(200, 40));
        
        addFundsButton = CustomButton.createSuccessButton("💰 Add Funds");
        addFundsButton.setPreferredSize(new Dimension(140, 45));
        withdrawButton = CustomButton.createDangerButton("💸 Withdraw");
        withdrawButton.setPreferredSize(new Dimension(140, 45));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.getBackgroundColor());
        
        // Professional header
        JPanel headerPanel = UIUtils.createHeaderPanel("💰 My Wallet");
        
        // Enhanced Balance Card with gradient background
        JPanel balanceCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(0, 0, UIUtils.PRIMARY, getWidth(), getHeight(), UIUtils.PRIMARY.brighter());
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        balanceCard.setLayout(new BorderLayout());
        balanceCard.setPreferredSize(new Dimension(0, 180));
        balanceCard.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD));
        
        JLabel balanceTitle = new JLabel("💰 Current Balance");
        balanceTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        balanceTitle.setForeground(Color.WHITE);
        balanceTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, UIUtils.SPACING_SM, 0));
        
        JPanel balanceInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, UIUtils.SPACING_XS, 0));
        balanceInfo.setOpaque(false);
        balanceInfo.add(balanceLabel);
        balanceInfo.add(currencyLabel);
        
        // Add balance change indicator
        JLabel changeLabel = new JLabel("📈 +$25.50 this week");
        changeLabel.setFont(UIUtils.FONT_SMALL);
        changeLabel.setForeground(new Color(200, 255, 200));
        
        JPanel balanceContent = new JPanel(new BorderLayout());
        balanceContent.setOpaque(false);
        balanceContent.add(balanceTitle, BorderLayout.NORTH);
        balanceContent.add(balanceInfo, BorderLayout.CENTER);
        balanceContent.add(changeLabel, BorderLayout.SOUTH);
        
        balanceCard.add(balanceContent, BorderLayout.CENTER);
        
        // Professional Operations Panel
        JPanel operationsPanel = UIUtils.createCard();
        operationsPanel.setLayout(new GridBagLayout());
        operationsPanel.setBackground(UIUtils.getSurfaceColor());
        operationsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIUtils.PRIMARY), 
            "Transfer Funds", 
            0, 0, UIUtils.FONT_HEADING, UIUtils.getTextColor()));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(UIUtils.SPACING_SM, UIUtils.SPACING_SM, UIUtils.SPACING_SM, UIUtils.SPACING_SM);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        JLabel selectCardLabel = new JLabel("Select Card:");
        selectCardLabel.setForeground(UIUtils.getTextColor());
        selectCardLabel.setFont(UIUtils.FONT_BODY);
        operationsPanel.add(selectCardLabel, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        operationsPanel.add(cardComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel amountLabel = new JLabel("Amount ($):");
        amountLabel.setForeground(UIUtils.getTextColor());
        amountLabel.setFont(UIUtils.FONT_BODY);
        operationsPanel.add(amountLabel, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        operationsPanel.add(amountField, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UIUtils.SPACING_SM, 0));
        buttonPanel.setBackground(UIUtils.getSurfaceColor());
        buttonPanel.add(addFundsButton);
        buttonPanel.add(withdrawButton);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        operationsPanel.add(buttonPanel, gbc);
        
        // Main Layout
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIUtils.getBackgroundColor());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIUtils.getBackgroundColor());
        topPanel.add(balanceCard, BorderLayout.CENTER);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, UIUtils.SPACING_MD, 0));
        
        // Bottom panel with operations and recent activity
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, UIUtils.SPACING_SM, 0));
        bottomPanel.setBackground(UIUtils.getBackgroundColor());
        
        // Recent Activity Panel
        JPanel recentActivityPanel = UIUtils.createCard();
        recentActivityPanel.setLayout(new BorderLayout());
        recentActivityPanel.setBackground(UIUtils.getSurfaceColor());
        recentActivityPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIUtils.SUCCESS), 
            "Recent Activity", 
            0, 0, UIUtils.FONT_HEADING, UIUtils.getTextColor()));
        
        // Real wallet activity list
        activityList = new JPanel();
        activityList.setLayout(new BoxLayout(activityList, BoxLayout.Y_AXIS));
        activityList.setBackground(UIUtils.getSurfaceColor());
        activityList.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_SM, UIUtils.SPACING_SM, UIUtils.SPACING_SM, UIUtils.SPACING_SM));
        
        updateRecentActivity(); // Load real data
        
        recentActivityPanel.add(activityList, BorderLayout.CENTER);
        
        bottomPanel.add(operationsPanel);
        bottomPanel.add(recentActivityPanel);
        
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(bottomPanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        addFundsButton.addActionListener(this::handleAddFunds);
        withdrawButton.addActionListener(this::handleWithdraw);
    }
    
    private void handleAddFunds(ActionEvent e) {
        String amountText = amountField.getText().trim();
        Card selectedCard = (Card) cardComboBox.getSelectedItem();
        
        if (amountText.isEmpty()) {
            UIUtils.showError(this, "Please enter an amount");
            return;
        }
        
        if (selectedCard == null) {
            UIUtils.showError(this, "Please select a card");
            return;
        }
        
        try {
            BigDecimal amount = new BigDecimal(amountText);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                UIUtils.showError(this, "Amount must be greater than zero");
                return;
            }
            
            if (UIUtils.showConfirmation(this, 
                String.format("Transfer $%.2f from %s to your wallet?", amount, selectedCard.getCardName()))) {
                
                mainController.getWalletController().addFunds(
                    Session.getCurrentUser().getUserId(), selectedCard.getCardId(), amount, "Card transfer");
                
                UIUtils.showSuccess(this, "Funds transferred successfully!");
                amountField.setText("");
                refreshData();
            }
            
        } catch (NumberFormatException ex) {
            UIUtils.showError(this, "Please enter a valid amount");
        } catch (Exception ex) {
            UIUtils.showError(this, "Failed to transfer funds: " + ex.getMessage());
        }
    }
    
    private void handleWithdraw(ActionEvent e) {
        String amountText = amountField.getText().trim();
        Card selectedCard = (Card) cardComboBox.getSelectedItem();
        
        if (amountText.isEmpty()) {
            UIUtils.showError(this, "Please enter an amount");
            return;
        }
        
        if (selectedCard == null) {
            UIUtils.showError(this, "Please select a card");
            return;
        }
        
        try {
            BigDecimal amount = new BigDecimal(amountText);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                UIUtils.showError(this, "Amount must be greater than zero");
                return;
            }
            
            if (UIUtils.showConfirmation(this, 
                String.format("Transfer $%.2f from wallet to %s?", amount, selectedCard.getCardName()))) {
                
                mainController.getWalletController().withdrawFunds(
                    Session.getCurrentUser().getUserId(), selectedCard.getCardId(), amount, "Card transfer");
                
                UIUtils.showSuccess(this, "Funds transferred successfully!");
                amountField.setText("");
                refreshData();
            }
            
        } catch (NumberFormatException ex) {
            UIUtils.showError(this, "Please enter a valid amount");
        } catch (Exception ex) {
            UIUtils.showError(this, "Failed to transfer funds: " + ex.getMessage());
        }
    }
    
    public void refreshData() {
        try {
            Wallet wallet = mainController.getWalletController().getWallet(Session.getCurrentUser().getUserId());
            balanceLabel.setText(String.format("$%.2f", wallet.getBalance()));
            currencyLabel.setText(wallet.getCurrency().toString());
            
            // Load user's active cards
            List<Card> cards = mainController.getCardController().getActiveCards(Session.getCurrentUser().getUserId());
            cardComboBox.removeAllItems();
            for (Card card : cards) {
                cardComboBox.addItem(card);
            }
            
            // Update recent activity with real data
            updateRecentActivity();
            
        } catch (Exception e) {
            UIUtils.showError(this, "Failed to load wallet data: " + e.getMessage());
        }
    }
    
    private void updateRecentActivity() {
        // Use EDT for UI updates
        SwingUtilities.invokeLater(() -> {
            activityList.removeAll();
            
            try {
                // Get recent transactions for wallet activity
                List<com.nfcpay.model.Transaction> transactions = mainController.getPaymentController()
                    .getTransactionHistory(Session.getCurrentUser().getUserId());
                
                // Show last 4 transactions
                int count = Math.min(4, transactions.size());
                for (int i = 0; i < count; i++) {
                    com.nfcpay.model.Transaction t = transactions.get(i);
                    addActivityItem(t);
                }
                
                if (transactions.isEmpty()) {
                    JLabel noActivity = new JLabel("No recent activity");
                    noActivity.setFont(UIUtils.FONT_SMALL);
                    noActivity.setForeground(UIUtils.NEUTRAL);
                    noActivity.setAlignmentX(Component.CENTER_ALIGNMENT);
                    activityList.add(noActivity);
                }
                
            } catch (Exception e) {
                JLabel errorLabel = new JLabel("Unable to load activity");
                errorLabel.setFont(UIUtils.FONT_SMALL);
                errorLabel.setForeground(UIUtils.DANGER);
                errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                activityList.add(errorLabel);
            }
            
            activityList.revalidate();
            activityList.repaint();
        });
    }
    
    private void addActivityItem(com.nfcpay.model.Transaction transaction) {
        JPanel activityItem = new JPanel(new BorderLayout());
        activityItem.setBackground(UIUtils.getSurfaceColor());
        activityItem.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        activityItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        // Amount with +/- indicator
        String amountText = String.format("%s$%.2f", 
            transaction.getTransactionType().toString().equals("PAYMENT") ? "-" : "+", 
            transaction.getAmount());
        
        JLabel amountLabel = new JLabel(amountText);
        amountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        amountLabel.setForeground(amountText.startsWith("+") ? UIUtils.SUCCESS : UIUtils.DANGER);
        
        // Transaction details
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBackground(UIUtils.getSurfaceColor());
        
        String icon = getTransactionIcon(transaction.getDescription());
        JLabel descLabel = new JLabel(icon + " " + transaction.getDescription());
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descLabel.setForeground(UIUtils.getTextColor());
        
        JLabel dateLabel = new JLabel(transaction.getCreatedAt().toLocalDate().toString());
        dateLabel.setFont(UIUtils.FONT_SMALL);
        dateLabel.setForeground(UIUtils.NEUTRAL);
        
        detailPanel.add(descLabel, BorderLayout.NORTH);
        detailPanel.add(dateLabel, BorderLayout.SOUTH);
        
        activityItem.add(amountLabel, BorderLayout.WEST);
        activityItem.add(detailPanel, BorderLayout.CENTER);
        
        activityList.add(activityItem);
    }
    
    private String getTransactionIcon(String description) {
        String desc = description.toLowerCase();
        if (desc.contains("starbucks") || desc.contains("coffee")) return "☕";
        if (desc.contains("amazon") || desc.contains("shopping")) return "📦";
        if (desc.contains("transfer") || desc.contains("card")) return "💳";
        if (desc.contains("gas") || desc.contains("shell")) return "⛽";
        if (desc.contains("food") || desc.contains("restaurant")) return "🍕";
        return "💰"; // Default wallet icon
    }
    
    // Custom renderer for card combo box
    private class CardComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                    boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Card) {
                Card card = (Card) value;
                setText(String.format("%s - $%.2f", card.getCardName(), card.getBalance()));
            }
            
            return this;
        }
    }
}