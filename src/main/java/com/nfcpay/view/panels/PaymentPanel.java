package com.nfcpay.view.panels;

import com.nfcpay.controller.MainController;
import com.nfcpay.model.Card;
import com.nfcpay.model.Merchant;
import com.nfcpay.model.Transaction;
import com.nfcpay.model.Wallet;
import com.nfcpay.util.Session;
import com.nfcpay.util.UIUtils;
import com.nfcpay.view.components.CustomButton;
import com.nfcpay.view.dialogs.PaymentConfirmDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.List;

/**
 * Payment Processing Panel
 */
public class PaymentPanel extends JPanel {
    private final MainController mainController;
    private JComboBox<Card> cardComboBox;
    private JComboBox<Merchant> merchantComboBox;
    private JTextField amountField;
    private JTextField descriptionField;
    private JButton payButton;
    private JButton nfcTapButton;
    private JLabel balanceLabel;
    
    public PaymentPanel(MainController mainController) {
        this.mainController = mainController;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        cardComboBox = new JComboBox<>();
        cardComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Card) {
                    Card card = (Card) value;
                    setText(card.getCardName() + " (" + card.getCardType() + ")");
                }
                return this;
            }
        });
        
        merchantComboBox = new JComboBox<>();
        merchantComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Merchant) {
                    Merchant merchant = (Merchant) value;
                    setText(merchant.getMerchantName() + " - " + merchant.getCategory());
                }
                return this;
            }
        });
        
        amountField = new JTextField(15);
        descriptionField = new JTextField(15);
        payButton = CustomButton.createPrimaryButton("💳 Regular Pay");
        payButton.setPreferredSize(new Dimension(180, 50));
        
        // Add NFC Tap button
        nfcTapButton = CustomButton.createSuccessButton("📱 Tap to Pay");
        nfcTapButton.setPreferredSize(new Dimension(180, 50));
        nfcTapButton.addActionListener(this::handleNFCTap);
        
        balanceLabel = new JLabel("Balance: $0.00");
        balanceLabel.setFont(UIUtils.FONT_BODY);
        
        // Enhanced form component styling
        Dimension fieldSize = new Dimension(300, 40);
        amountField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        amountField.setPreferredSize(fieldSize);
        descriptionField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        descriptionField.setPreferredSize(fieldSize);
        cardComboBox.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cardComboBox.setPreferredSize(fieldSize);
        merchantComboBox.setFont(new Font("SansSerif", Font.PLAIN, 16));
        merchantComboBox.setPreferredSize(fieldSize);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.getBackgroundColor());
        
        // Professional header
        JPanel headerPanel = UIUtils.createHeaderPanel("💸 Make Payment");
        
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setFont(UIUtils.FONT_HEADING);
        headerPanel.add(balanceLabel, BorderLayout.EAST);
        
        // Professional Payment Form
        JPanel formPanel = UIUtils.createCard();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(UIUtils.getSurfaceColor());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIUtils.PRIMARY), 
            "Payment Details", 
            0, 0, UIUtils.FONT_HEADING, UIUtils.getTextColor()));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(UIUtils.SPACING_SM, UIUtils.SPACING_SM, UIUtils.SPACING_SM, UIUtils.SPACING_SM);
        
        // Card Selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        JLabel cardLabel = new JLabel("💳 Pay with Card:");
        cardLabel.setForeground(UIUtils.getTextColor());
        cardLabel.setFont(UIUtils.FONT_BODY);
        formPanel.add(cardLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cardComboBox, gbc);
        
        // Merchant Selection
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel merchantLabel = new JLabel("🏦 Pay to Merchant:");
        merchantLabel.setForeground(UIUtils.getTextColor());
        merchantLabel.setFont(UIUtils.FONT_BODY);
        formPanel.add(merchantLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(merchantComboBox, gbc);
        
        // Amount
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        JLabel amountLabel = new JLabel("💰 Amount ($):");
        amountLabel.setForeground(UIUtils.getTextColor());
        amountLabel.setFont(UIUtils.FONT_BODY);
        formPanel.add(amountLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(amountField, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        JLabel descLabel = new JLabel("📝 Description:");
        descLabel.setForeground(UIUtils.getTextColor());
        descLabel.setFont(UIUtils.FONT_BODY);
        formPanel.add(descLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(descriptionField, gbc);
        
        // Payment Buttons - Better alignment
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, UIUtils.SPACING_SM, 0));
        buttonPanel.setBackground(UIUtils.getSurfaceColor());
        buttonPanel.setPreferredSize(new Dimension(380, 50));
        buttonPanel.add(payButton);
        buttonPanel.add(nfcTapButton);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(UIUtils.SPACING_MD, UIUtils.SPACING_SM, UIUtils.SPACING_SM, UIUtils.SPACING_SM);
        formPanel.add(buttonPanel, gbc);
        
        // Enhanced Quick merchants panel
        JPanel quickMerchantsPanel = UIUtils.createCard();
        quickMerchantsPanel.setLayout(new BorderLayout());
        quickMerchantsPanel.setBackground(UIUtils.getSurfaceColor());
        
        JLabel quickTitle = new JLabel("⚡ Quick Pay");
        quickTitle.setFont(UIUtils.FONT_HEADING);
        quickTitle.setForeground(UIUtils.getTextColor());
        quickTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, UIUtils.SPACING_SM, 0));
        
        JPanel buttonGrid = new JPanel(new GridLayout(2, 2, UIUtils.SPACING_SM, UIUtils.SPACING_SM));
        buttonGrid.setBackground(UIUtils.getSurfaceColor());
        
        // Enhanced quick merchant buttons
        JButton starbucksBtn = new CustomButton("☕ Starbucks - $4.50", CustomButton.ButtonStyle.SUCCESS);
        JButton amazonBtn = new CustomButton("📦 Amazon - $29.99", CustomButton.ButtonStyle.PRIMARY);
        JButton shellBtn = new CustomButton("⛽ Shell - $45.00", CustomButton.ButtonStyle.WARNING);
        JButton pizzaBtn = new CustomButton("🍕 Pizza Hut - $18.50", CustomButton.ButtonStyle.DANGER);
        
        Dimension btnSize = new Dimension(200, 45);
        starbucksBtn.setPreferredSize(btnSize);
        amazonBtn.setPreferredSize(btnSize);
        shellBtn.setPreferredSize(btnSize);
        pizzaBtn.setPreferredSize(btnSize);
        
        // Add click handlers for quick merchants
        starbucksBtn.addActionListener(e -> selectQuickMerchant("Starbucks", "4.50"));
        amazonBtn.addActionListener(e -> selectQuickMerchant("Amazon", "29.99"));
        shellBtn.addActionListener(e -> selectQuickMerchant("Shell", "45.00"));
        pizzaBtn.addActionListener(e -> selectQuickMerchant("Pizza Hut", "18.50"));
        
        buttonGrid.add(starbucksBtn);
        buttonGrid.add(amazonBtn);
        buttonGrid.add(shellBtn);
        buttonGrid.add(pizzaBtn);
        
        quickMerchantsPanel.add(quickTitle, BorderLayout.NORTH);
        quickMerchantsPanel.add(buttonGrid, BorderLayout.CENTER);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIUtils.getBackgroundColor());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD));
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(quickMerchantsPanel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        payButton.addActionListener(this::handlePayment);
    }
    
    private void handleNFCTap(ActionEvent e) {
        Card selectedCard = (Card) cardComboBox.getSelectedItem();
        Merchant selectedMerchant = (Merchant) merchantComboBox.getSelectedItem();
        String amountText = amountField.getText().trim();
        
        // Validation
        if (selectedCard == null) {
            UIUtils.showError(this, "Please select a card first");
            return;
        }
        
        if (selectedMerchant == null) {
            UIUtils.showError(this, "Please select a merchant first");
            return;
        }
        
        if (amountText.isEmpty()) {
            UIUtils.showError(this, "Please enter an amount first");
            return;
        }
        
        try {
            BigDecimal amount = new BigDecimal(amountText);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                UIUtils.showError(this, "Amount must be greater than zero");
                return;
            }
            
            // Start NFC simulation
            simulateNFCTap(selectedCard, selectedMerchant, amount);
            
        } catch (NumberFormatException ex) {
            UIUtils.showError(this, "Please enter a valid amount");
        }
    }
    
    private void simulateNFCTap(Card card, Merchant merchant, BigDecimal amount) {
        // Create NFC simulation dialog
        JDialog nfcDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "NFC Payment", true);
        nfcDialog.setSize(400, 300);
        nfcDialog.setLocationRelativeTo(this);
        nfcDialog.setLayout(new BorderLayout());
        
        // NFC Animation Panel
        JPanel animationPanel = new JPanel(new BorderLayout());
        animationPanel.setBackground(UIUtils.PRIMARY);
        animationPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel nfcIcon = new JLabel("📱", SwingConstants.CENTER);
        nfcIcon.setFont(new Font("SansSerif", Font.PLAIN, 80));
        nfcIcon.setForeground(Color.WHITE);
        
        JLabel statusLabel = new JLabel("Bring device closer to NFC reader...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setForeground(Color.WHITE);
        
        JLabel amountLabel = new JLabel(String.format("Amount: $%.2f", amount), SwingConstants.CENTER);
        amountLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        amountLabel.setForeground(new Color(200, 255, 200));
        
        animationPanel.add(nfcIcon, BorderLayout.CENTER);
        animationPanel.add(statusLabel, BorderLayout.SOUTH);
        animationPanel.add(amountLabel, BorderLayout.NORTH);
        
        nfcDialog.add(animationPanel, BorderLayout.CENTER);
        
        // Show dialog and start simulation
        SwingUtilities.invokeLater(() -> {
            nfcDialog.setVisible(true);
        });
        
        // Simulate NFC detection after 2 seconds
        Timer timer = new Timer(2000, e -> {
            statusLabel.setText("✅ NFC Payment Detected!");
            nfcIcon.setText("✅");
            
            // Process payment after another 1 second
            Timer paymentTimer = new Timer(1000, payEvent -> {
                nfcDialog.dispose();
                processNFCPayment(card, merchant, amount);
            });
            paymentTimer.setRepeats(false);
            paymentTimer.start();
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void processNFCPayment(Card card, Merchant merchant, BigDecimal amount) {
        try {
            // Process the actual payment
            Transaction transaction = mainController.getPaymentController().processPayment(
                Session.getCurrentUser().getUserId(),
                card.getCardId(),
                merchant.getMerchantId(),
                amount,
                "NFC Contactless Payment"
            );
            
            // Success message with NFC branding
            String successMessage = String.format(
                "✅ NFC Payment Successful!\n\n" +
                "📱 Contactless Payment\n" +
                "Transaction ID: %d\n" +
                "Amount: $%.2f\n" +
                "Card: %s\n" +
                "Merchant: %s\n" +
                "Status: %s",
                transaction.getTransactionId(),
                transaction.getAmount(),
                card.getCardName(),
                merchant.getMerchantName(),
                transaction.getStatus()
            );
            
            UIUtils.showSuccess(this, successMessage);
            
            // Clear form
            amountField.setText("");
            descriptionField.setText("");
            refreshData();
            
        } catch (Exception ex) {
            UIUtils.showError(this, "NFC Payment failed: " + ex.getMessage());
        }
    }
    
    private void handlePayment(ActionEvent e) {
        Card selectedCard = (Card) cardComboBox.getSelectedItem();
        Merchant selectedMerchant = (Merchant) merchantComboBox.getSelectedItem();
        String amountText = amountField.getText().trim();
        String description = descriptionField.getText().trim();
        
        // Validation
        if (selectedCard == null) {
            UIUtils.showError(this, "Please select a card");
            return;
        }
        
        if (selectedMerchant == null) {
            UIUtils.showError(this, "Please select a merchant");
            return;
        }
        
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
            
            // Confirmation Dialog
            PaymentConfirmDialog confirmDialog = new PaymentConfirmDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                selectedCard, selectedMerchant, amount,
                description.isEmpty() ? "NFC Payment" : description
            );
            confirmDialog.setVisible(true);
            
            if (confirmDialog.isConfirmed()) {
                // Process payment
                Transaction transaction = mainController.getPaymentController().processPayment(
                    Session.getCurrentUser().getUserId(),
                    selectedCard.getCardId(),
                    selectedMerchant.getMerchantId(),
                    amount,
                    description.isEmpty() ? null : description
                );
                
                // Success message
                String successMessage = String.format(
                    "Payment Successful!\n\n" +
                    "Transaction ID: %d\n" +
                    "Amount: $%.2f\n" +
                    "Status: %s",
                    transaction.getTransactionId(),
                    transaction.getAmount(),
                    transaction.getStatus()
                );
                
                UIUtils.showSuccess(this, successMessage);
                
                // Clear form
                amountField.setText("");
                descriptionField.setText("");
                refreshData();
            }
            
        } catch (NumberFormatException ex) {
            UIUtils.showError(this, "Please enter a valid amount");
        } catch (Exception ex) {
            UIUtils.showError(this, "Payment failed: " + ex.getMessage());
        }
    }
    
    public void refreshData() {
        try {
            int userId = Session.getCurrentUser().getUserId();
            
            // Load cards
            try {
                List<Card> cards = mainController.getCardController().getUserCards(userId);
                cardComboBox.removeAllItems();
                for (Card card : cards) {
                    if (card.isActive()) {
                        cardComboBox.addItem(card);
                    }
                }
            } catch (Exception ex) {
                System.err.println("Error loading cards: " + ex.getMessage());
            }
            
            // Load merchants
            try {
                List<Merchant> merchants = mainController.getPaymentController().getActiveMerchants();
                merchantComboBox.removeAllItems();
                for (Merchant merchant : merchants) {
                    merchantComboBox.addItem(merchant);
                }
            } catch (Exception ex) {
                System.err.println("Error loading merchants: " + ex.getMessage());
            }
            
            // Update balance
            try {
                Wallet wallet = mainController.getWalletController().getWallet(userId);
                balanceLabel.setText(String.format("Balance: $%.2f", wallet.getBalance()));
            } catch (Exception ex) {
                balanceLabel.setText("Balance: $0.00");
            }
            
        } catch (Exception e) {
            UIUtils.showError(this, "Failed to load payment data: " + e.getMessage());
        }
    }
    
    private void selectQuickMerchant(String merchantName, String amount) {
        // Find and select merchant
        for (int i = 0; i < merchantComboBox.getItemCount(); i++) {
            Merchant merchant = merchantComboBox.getItemAt(i);
            if (merchant.getMerchantName().toLowerCase().contains(merchantName.toLowerCase())) {
                merchantComboBox.setSelectedItem(merchant);
                break;
            }
        }
        
        // Set amount
        amountField.setText(amount);
        
        // Set description
        descriptionField.setText("Payment to " + merchantName);
        
        // Focus on pay button
        payButton.requestFocus();
    }
}