package com.nfcpay.view.panels;

import com.nfcpay.controller.MainController;
import com.nfcpay.model.Card;
import com.nfcpay.model.enums.CardType;
import com.nfcpay.util.Session;
import com.nfcpay.util.NotificationManager;
import com.nfcpay.util.UIUtils;

import com.nfcpay.view.components.*;
import com.nfcpay.view.dialogs.ConfirmationDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Card Management Panel
 */
public class CardPanel extends JPanel {
    private final MainController mainController;
    private JPanel cardGridPanel;
    private StatusPanel statusPanel;
    private JButton addButton, editButton, deleteButton, toggleStatusButton;
    private List<Card> allCards;
    private Card selectedCard;
    
    public CardPanel(MainController mainController) {
        this.mainController = mainController;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        statusPanel = new StatusPanel();
        
        addButton = new CustomButton("➕ Add Card", CustomButton.ButtonStyle.SUCCESS);
        editButton = new CustomButton("✏️ Edit", CustomButton.ButtonStyle.PRIMARY);
        deleteButton = new CustomButton("🗑️ Delete", CustomButton.ButtonStyle.DANGER);
        toggleStatusButton = new CustomButton("🔄 Toggle", CustomButton.ButtonStyle.WARNING);
        
        // Initially disable action buttons
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        toggleStatusButton.setEnabled(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.getBackgroundColor());
        
        // Professional header
        JPanel headerPanel = UIUtils.createHeaderPanel("💳 My Cards");
        headerPanel.add(addButton, BorderLayout.EAST);
        
        // Professional card grid instead of table
        JPanel cardGridPanel = new JPanel(new GridLayout(0, 3, UIUtils.SPACING_SM, UIUtils.SPACING_SM));
        cardGridPanel.setBackground(UIUtils.getBackgroundColor());
        cardGridPanel.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD));
        
        JScrollPane scrollPane = new JScrollPane(cardGridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIUtils.getBackgroundColor());
        scrollPane.setBackground(UIUtils.getBackgroundColor());
        
        this.cardGridPanel = cardGridPanel; // Store reference for refreshData
        
        // Action buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UIUtils.SPACING_SM, 0));
        buttonPanel.setBackground(UIUtils.getBackgroundColor());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_SM, 0, UIUtils.SPACING_SM, 0));
        buttonPanel.add(editButton);
        buttonPanel.add(toggleStatusButton);
        buttonPanel.add(deleteButton);
        
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIUtils.getBackgroundColor());
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.PAGE_END);
    }
    
    private void setupEventHandlers() {
        // Remove search functionality
        
        // Button actions
        addButton.addActionListener(this::handleAddCard);
        editButton.addActionListener(this::handleEditCard);
        deleteButton.addActionListener(this::handleDeleteCard);
        toggleStatusButton.addActionListener(this::handleToggleStatus);
        
        // No table selection needed for card grid
    }
    
    private void handleAddCard(ActionEvent e) {
        try {
            if (!mainController.getCardController().canAddMoreCards(Session.getCurrentUser().getUserId())) {
                JOptionPane.showMessageDialog(this, "Maximum 5 cards allowed per user", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            handleAddCardDialog();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to add card: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleEditCard(ActionEvent e) {
        if (selectedCard == null) return;
        
        try {
            String currentName = selectedCard.getCardName();
            
            String newName = JOptionPane.showInputDialog(this, "Enter new card name:", currentName);
            if (newName != null && !newName.trim().isEmpty()) {
                mainController.getCardController().updateCard(selectedCard.getCardId(), 
                    Session.getCurrentUser().getUserId(), newName.trim());
                JOptionPane.showMessageDialog(this, "Card updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to update card: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleToggleStatus(ActionEvent e) {
        if (selectedCard == null) return;
        
        String action = selectedCard.isActive() ? "deactivate" : "activate";
        
        if (JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to " + action + " this card?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            
            try {
                statusPanel.showProgress("Updating card status...", 50);
                
                if (selectedCard.isActive()) {
                    mainController.getCardController().deactivateCard(selectedCard.getCardId(), 
                        Session.getCurrentUser().getUserId());
                } else {
                    mainController.getCardController().activateCard(selectedCard.getCardId(), 
                        Session.getCurrentUser().getUserId());
                }
                
                statusPanel.showProgress("Status updated", 100);
                JOptionPane.showMessageDialog(this, "Card status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                selectedCard = null; // Clear selection
                refreshData();
                
            } catch (Exception ex) {
                statusPanel.showStatus("Failed to update status", StatusPanel.StatusType.ERROR);
                JOptionPane.showMessageDialog(this, "Failed to update card status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleDeleteCard(ActionEvent e) {
        if (selectedCard == null) return;
        
        if (ConfirmationDialog.showConfirmation(this, "Delete Card", 
            "Are you sure you want to delete the card '" + selectedCard.getCardName() + "'?", 
            "Delete")) {
            
            try {
                statusPanel.showProgress("Deleting card...", 50);
                mainController.getCardController().deleteCard(selectedCard.getCardId(), 
                    Session.getCurrentUser().getUserId());
                statusPanel.showProgress("Card deleted", 100);
                
                JOptionPane.showMessageDialog(this, "Card deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                selectedCard = null; // Clear selection
                refreshData();
                
            } catch (Exception ex) {
                statusPanel.showStatus("Failed to delete card", StatusPanel.StatusType.ERROR);
                JOptionPane.showMessageDialog(this, "Failed to delete card: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateButtonStates() {
        boolean hasSelection = selectedCard != null;
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
        toggleStatusButton.setEnabled(hasSelection);
        
        if (hasSelection) {
            toggleStatusButton.setText(selectedCard.isActive() ? "🔴 Deactivate" : "🟢 Activate");
        }
    }
    
    public void refreshData() {
        statusPanel.showProgress("Loading cards...", 0);
        
        SwingWorker<List<Card>, Void> worker = new SwingWorker<List<Card>, Void>() {
            @Override
            protected List<Card> doInBackground() throws Exception {
                statusPanel.showProgress("Fetching card data...", 50);
                return mainController.getCardController()
                    .getUserCards(Session.getCurrentUser().getUserId());
            }
            
            @Override
            protected void done() {
                try {
                    allCards = get();
                    updateCardGrid();
                    
                    long activeCards = allCards.stream().mapToLong(card -> card.isActive() ? 1 : 0).sum();
                    double totalBalance = allCards.stream().mapToDouble(card -> card.getBalance().doubleValue()).sum();
                    
                    statusPanel.showProgress("Complete", 100);
                    statusPanel.showStatus("Loaded " + allCards.size() + " cards (" + 
                                         activeCards + " active, $" + String.format("%.2f", totalBalance) + " total)", 
                                         StatusPanel.StatusType.SUCCESS);
                    
                } catch (Exception e) {
                    statusPanel.showStatus("Failed to load cards", StatusPanel.StatusType.ERROR);
                    JOptionPane.showMessageDialog(CardPanel.this, "Failed to load cards: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void updateCardGrid() {
        cardGridPanel.removeAll();
        
        for (Card card : allCards) {
            JPanel cardComponent = createCardComponent(card);
            cardGridPanel.add(cardComponent);
        }
        
        cardGridPanel.revalidate();
        cardGridPanel.repaint();
    }
    
    private JPanel createCardComponent(Card card) {
        JPanel cardPanel = UIUtils.createCard();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBackground(UIUtils.getSurfaceColor());
        cardPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Card header with type icon
        JLabel typeLabel = new JLabel(getCardIcon(card.getCardType()) + " " + card.getCardType());
        typeLabel.setFont(UIUtils.FONT_SMALL);
        typeLabel.setForeground(UIUtils.NEUTRAL);
        
        // Card name
        JLabel nameLabel = new JLabel(card.getCardName());
        nameLabel.setFont(UIUtils.FONT_HEADING);
        nameLabel.setForeground(UIUtils.getTextColor());
        
        // Card number (masked)
        JLabel numberLabel = new JLabel("**** **** **** " + card.getCardUid().substring(Math.max(0, card.getCardUid().length() - 4)));
        numberLabel.setFont(UIUtils.FONT_BODY);
        numberLabel.setForeground(UIUtils.NEUTRAL);
        
        // Balance
        JLabel balanceLabel = new JLabel(String.format("$%.2f", card.getBalance()));
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        balanceLabel.setForeground(UIUtils.SUCCESS);
        
        // Status indicator
        JLabel statusLabel = new JLabel(card.isActive() ? "🟢 Active" : "🔴 Inactive");
        statusLabel.setFont(UIUtils.FONT_SMALL);
        statusLabel.setForeground(card.isActive() ? UIUtils.SUCCESS : UIUtils.DANGER);
        
        // Layout
        JPanel infoPanel = new JPanel(new GridLayout(5, 1, 0, UIUtils.SPACING_XS));
        infoPanel.setBackground(UIUtils.getSurfaceColor());
        infoPanel.add(typeLabel);
        infoPanel.add(nameLabel);
        infoPanel.add(numberLabel);
        infoPanel.add(balanceLabel);
        infoPanel.add(statusLabel);
        
        cardPanel.add(infoPanel, BorderLayout.CENTER);
        
        // Click handler
        cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectCard(card, cardPanel);
            }
        });
        
        return cardPanel;
    }
    
    private String getCardIcon(CardType cardType) {
        switch (cardType) {
            case PHYSICAL: return "💳";
            case VIRTUAL: return "📱";
            default: return "💳";
        }
    }
    
    private void selectCard(Card card, JPanel cardPanel) {
        // Clear previous selection
        if (selectedCard != null) {
            for (Component comp : cardGridPanel.getComponents()) {
                if (comp instanceof JPanel) {
                    ((JPanel)comp).setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0xe2e8f0), 1),
                        BorderFactory.createEmptyBorder(UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD)
                    ));
                }
            }
        }
        
        // Select new card
        selectedCard = card;
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.PRIMARY, 2),
            BorderFactory.createEmptyBorder(UIUtils.SPACING_MD-2, UIUtils.SPACING_MD-2, UIUtils.SPACING_MD-2, UIUtils.SPACING_MD-2)
        ));
        
        // Update status message
        statusPanel.showStatus("Selected: " + card.getCardName() + " - $" + String.format("%.2f", card.getBalance()), StatusPanel.StatusType.INFO);
        
        updateButtonStates();
    }
    

    
    // Search functionality removed for card grid layout
    
    private void handleAddCardDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Card", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        JTextField nameField = new JTextField(20);
        // Card number field removed - using auto-generated UID
        JComboBox<CardType> typeCombo = new JComboBox<>(CardType.values());
        JTextField balanceField = new JTextField("0.00", 20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Card Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        

        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Card Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(typeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Initial Balance:"), gbc);
        gbc.gridx = 1;
        formPanel.add(balanceField, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(248, 249, 250));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 221, 222)));
        
        CustomButton saveButton = new CustomButton("💾 Save Card", CustomButton.ButtonStyle.SUCCESS);
        CustomButton cancelButton = new CustomButton("❌ Cancel", CustomButton.ButtonStyle.SECONDARY);
        
        saveButton.addActionListener(e -> {
            try {
                if (nameField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Card name is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                

                
                double balance = Double.parseDouble(balanceField.getText());
                if (balance < 0) {
                    JOptionPane.showMessageDialog(dialog, "Balance cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                statusPanel.showProgress("Creating card...", 50);
                
                mainController.getCardController().addCard(
                    Session.getCurrentUser().getUserId(),
                    nameField.getText().trim(),
                    (CardType) typeCombo.getSelectedItem(),
                    new java.math.BigDecimal(balance));
                
                statusPanel.showProgress("Card created", 100);
                JOptionPane.showMessageDialog(this, "Card added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                dialog.dispose();
                refreshData();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid balance amount", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                statusPanel.showStatus("Failed to create card", StatusPanel.StatusType.ERROR);
                JOptionPane.showMessageDialog(dialog, "Failed to create card: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}