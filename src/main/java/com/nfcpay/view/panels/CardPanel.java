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
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setBackground(UIUtils.getBackgroundColor());
        
        // Professional Header
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(0, 0, UIUtils.SUCCESS, getWidth(), 0, UIUtils.SUCCESS.brighter());
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD));
        
        JLabel titleLabel = new JLabel("💳 Add New Card");
        titleLabel.setFont(UIUtils.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Professional Form Card
        JPanel formCard = UIUtils.createCard();
        formCard.setLayout(new GridBagLayout());
        formCard.setBackground(UIUtils.getSurfaceColor());
        formCard.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(UIUtils.SPACING_SM, UIUtils.SPACING_SM, UIUtils.SPACING_SM, UIUtils.SPACING_SM);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Enhanced form fields
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        nameField.setPreferredSize(new Dimension(300, 40));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JComboBox<CardType> typeCombo = new JComboBox<>(CardType.values());
        typeCombo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        typeCombo.setPreferredSize(new Dimension(300, 40));
        
        JTextField balanceField = new JTextField("0.00", 20);
        balanceField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        balanceField.setPreferredSize(new Dimension(300, 40));
        balanceField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Form layout with icons
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel("📝 Card Name:");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setForeground(UIUtils.getTextColor());
        formCard.add(nameLabel, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formCard.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel typeLabel = new JLabel("💳 Card Type:");
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        typeLabel.setForeground(UIUtils.getTextColor());
        formCard.add(typeLabel, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formCard.add(typeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel balanceLabel = new JLabel("💰 Initial Balance:");
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        balanceLabel.setForeground(UIUtils.getTextColor());
        formCard.add(balanceLabel, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formCard.add(balanceField, gbc);
        
        // Professional Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, UIUtils.SPACING_MD, UIUtils.SPACING_SM));
        buttonPanel.setBackground(UIUtils.getBackgroundColor());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_SM, 0, UIUtils.SPACING_SM, 0));
        
        CustomButton saveButton = new CustomButton("✅ Save Card", CustomButton.ButtonStyle.SUCCESS);
        saveButton.setPreferredSize(new Dimension(140, 45));
        CustomButton cancelButton = new CustomButton("❌ Cancel", CustomButton.ButtonStyle.SECONDARY);
        cancelButton.setPreferredSize(new Dimension(120, 45));
        
        saveButton.addActionListener(e -> {
            try {
                if (nameField.getText().trim().isEmpty()) {
                    UIUtils.showError(dialog, "Card name is required");
                    return;
                }
                
                double balance = Double.parseDouble(balanceField.getText());
                if (balance < 0) {
                    UIUtils.showError(dialog, "Balance cannot be negative");
                    return;
                }
                
                statusPanel.showProgress("Creating card...", 50);
                
                mainController.getCardController().addCard(
                    Session.getCurrentUser().getUserId(),
                    nameField.getText().trim(),
                    (CardType) typeCombo.getSelectedItem(),
                    new java.math.BigDecimal(balance));
                
                statusPanel.showProgress("Card created", 100);
                UIUtils.showSuccess(this, "Card added successfully!");
                
                dialog.dispose();
                refreshData();
                
            } catch (NumberFormatException ex) {
                UIUtils.showError(dialog, "Please enter a valid balance amount");
            } catch (Exception ex) {
                statusPanel.showStatus("Failed to create card", StatusPanel.StatusType.ERROR);
                UIUtils.showError(dialog, "Failed to create card: " + ex.getMessage());
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Main container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(UIUtils.getBackgroundColor());
        mainContainer.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD));
        mainContainer.add(formCard, BorderLayout.CENTER);
        
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(mainContainer, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}