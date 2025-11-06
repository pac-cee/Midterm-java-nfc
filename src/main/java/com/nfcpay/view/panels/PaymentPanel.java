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
        payButton = CustomButton.createPrimaryButton("Make Payment");
        balanceLabel = new JLabel("Balance: $0.00");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
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
        
        JLabel titleLabel = new JLabel("💳 Make Payment");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(balanceLabel, BorderLayout.EAST);
        
        // Payment Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Payment Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Card Selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Select Card:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cardComboBox, gbc);
        
        // Merchant Selection
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Select Merchant:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(merchantComboBox, gbc);
        
        // Amount
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Amount ($):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(amountField, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(descriptionField, gbc);
        
        // Pay Button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(payButton, gbc);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        contentPanel.add(formPanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        payButton.addActionListener(this::handlePayment);
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
                List<Merchant> merchants = mainController.getMerchantController().getAllMerchants();
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
}