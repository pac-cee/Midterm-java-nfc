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
        
        // Activity list
        String[] activities = {
            "+$100.00  Card Transfer    Nov 28",
            "-$4.50    Starbucks       Nov 28", 
            "-$29.99   Amazon          Nov 27",
            "+$50.00   Card Transfer    Nov 26"
        };
        
        JPanel activityList = new JPanel(new GridLayout(activities.length, 1, 0, UIUtils.SPACING_XS));
        activityList.setBackground(UIUtils.getSurfaceColor());
        activityList.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_SM, UIUtils.SPACING_SM, UIUtils.SPACING_SM, UIUtils.SPACING_SM));
        
        for (String activity : activities) {
            JLabel activityLabel = new JLabel(activity);
            activityLabel.setFont(UIUtils.FONT_SMALL);
            activityLabel.setForeground(activity.startsWith("+") ? UIUtils.SUCCESS : UIUtils.DANGER);
            activityList.add(activityLabel);
        }
        
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
            
        } catch (Exception e) {
            UIUtils.showError(this, "Failed to load wallet data: " + e.getMessage());
        }
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