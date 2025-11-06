package com.nfcpay.view.dialogs;

import com.nfcpay.controller.MainController;
import com.nfcpay.model.enums.CardType;
import com.nfcpay.util.Session;
import com.nfcpay.util.UIUtils;
import com.nfcpay.view.components.CustomButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Add Card Dialog
 */
public class AddCardDialog extends JDialog {
    private final MainController mainController;
    private final JFrame parent;
    private JTextField cardNameField;
    private JTextField balanceField;
    private JComboBox<CardType> cardTypeCombo;
    private CustomButton addButton;
    private CustomButton cancelButton;
    private boolean success = false;
    
    public AddCardDialog(JFrame parent, MainController mainController) {
        super(parent, "Add New Card", true);
        this.parent = parent;
        this.mainController = mainController;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupDialog();
    }
    
    private void initializeComponents() {
        cardNameField = new JTextField(20);
        balanceField = new JTextField(20);
        balanceField.setText("0.00");
        cardTypeCombo = new JComboBox<>(CardType.values());
        addButton = CustomButton.createSuccessButton("Add Card");
        cancelButton = CustomButton.createSecondaryButton("Cancel");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Card Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Card Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cardNameField, gbc);
        
        // Initial Balance
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Initial Balance:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(balanceField, gbc);
        
        // Card Type
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Card Type:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cardTypeCombo, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        addButton.addActionListener(this::handleAdd);
        cancelButton.addActionListener(e -> dispose());
        
        // Enter key support
        getRootPane().setDefaultButton(addButton);
    }
    
    private void setupDialog() {
        setSize(350, 250);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void handleAdd(ActionEvent e) {
        String cardName = cardNameField.getText().trim();
        String balanceText = balanceField.getText().trim();
        CardType cardType = (CardType) cardTypeCombo.getSelectedItem();
        
        if (cardName.isEmpty()) {
            UIUtils.showError(this, "Please enter a card name");
            return;
        }
        
        try {
            java.math.BigDecimal initialBalance = new java.math.BigDecimal(balanceText);
            if (initialBalance.compareTo(java.math.BigDecimal.ZERO) < 0) {
                UIUtils.showError(this, "Initial balance cannot be negative");
                return;
            }
            
            mainController.getCardController().addCard(
                Session.getCurrentUser().getUserId(), cardName, cardType, initialBalance);
            success = true;
            dispose();
            
        } catch (NumberFormatException ex) {
            UIUtils.showError(this, "Please enter a valid balance amount");
        } catch (Exception ex) {
            UIUtils.showError(this, "Failed to add card: " + ex.getMessage());
        }
    }
    
    public boolean isSuccess() {
        return success;
    }
}