package com.nfcpay.view.dialogs;

import com.nfcpay.model.Card;
import com.nfcpay.model.Merchant;
import com.nfcpay.view.components.CustomButton;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Payment Confirmation Dialog
 */
public class PaymentConfirmDialog extends JDialog {
    private boolean confirmed = false;
    
    public PaymentConfirmDialog(JFrame parent, Card card, Merchant merchant, 
                               BigDecimal amount, String description) {
        super(parent, "Confirm Payment", true);
        setupDialog(card, merchant, amount, description);
    }
    
    private void setupDialog(Card card, Merchant merchant, BigDecimal amount, String description) {
        setLayout(new BorderLayout());
        
        // Content Panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        JLabel titleLabel = new JLabel("Confirm Payment Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        contentPanel.add(titleLabel, gbc);
        
        // Payment details
        gbc.gridwidth = 1; gbc.gridy++;
        contentPanel.add(new JLabel("Card:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(new JLabel(card.getCardName() + " (" + card.getCardType() + ")"), gbc);
        
        gbc.gridx = 0; gbc.gridy++;
        contentPanel.add(new JLabel("Merchant:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(new JLabel(merchant.getMerchantName()), gbc);
        
        gbc.gridx = 0; gbc.gridy++;
        contentPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        JLabel amountLabel = new JLabel(String.format("$%.2f", amount));
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        amountLabel.setForeground(new Color(220, 53, 69));
        contentPanel.add(amountLabel, gbc);
        
        if (description != null && !description.isEmpty()) {
            gbc.gridx = 0; gbc.gridy++;
            contentPanel.add(new JLabel("Description:"), gbc);
            gbc.gridx = 1;
            contentPanel.add(new JLabel(description), gbc);
        }
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        CustomButton confirmButton = CustomButton.createSuccessButton("Confirm Payment");
        CustomButton cancelButton = CustomButton.createSecondaryButton("Cancel");
        
        confirmButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setSize(400, 250);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getRootPane().setDefaultButton(confirmButton);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}