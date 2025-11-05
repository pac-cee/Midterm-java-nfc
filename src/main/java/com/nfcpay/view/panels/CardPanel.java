package com.nfcpay.view.panels;

import com.nfcpay.controller.MainController;
import com.nfcpay.model.Card;
import com.nfcpay.model.enums.CardType;
import com.nfcpay.util.Session;
import com.nfcpay.util.NotificationManager;
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
    private ModernTable cardTable;
    private ModernTableModel<Card> tableModel;
    private SearchPanel searchPanel;
    private StatusPanel statusPanel;
    private JButton addButton, editButton, deleteButton, toggleStatusButton;
    private List<Card> allCards;
    
    public CardPanel(MainController mainController) {
        this.mainController = mainController;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        String[] columns = {"ID", "Card Name", "Card Number", "Type", "Status", "Balance"};
        
        tableModel = new ModernTableModel<>(columns, card -> new Object[]{
            card.getCardId(),
            card.getCardName(),
            "**** **** **** " + card.getCardNumber().substring(card.getCardNumber().length() - 4),
            card.getCardType(),
            card.isActive() ? "Active" : "Inactive",
            String.format("$%.2f", card.getBalance())
        });
        
        cardTable = new ModernTable(tableModel);
        searchPanel = new SearchPanel("Search cards by name, type, or status...");
        statusPanel = new StatusPanel();
        
        addButton = new CustomButton("➕ Add Card", CustomButton.ButtonStyle.SUCCESS);
        editButton = new CustomButton("✏️ Edit Card", CustomButton.ButtonStyle.PRIMARY);
        deleteButton = new CustomButton("🗑️ Delete Card", CustomButton.ButtonStyle.DANGER);
        toggleStatusButton = new CustomButton("🔄 Toggle Status", CustomButton.ButtonStyle.WARNING);
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
        
        JLabel titleLabel = new JLabel("💳 Card Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(addButton, BorderLayout.EAST);
        
        // Remove search panel - not needed for cards
        
        // Table container
        JScrollPane scrollPane = new JScrollPane(cardTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        // Action buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        buttonPanel.add(editButton);
        buttonPanel.add(toggleStatusButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(deleteButton);
        
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(tableContainer, BorderLayout.CENTER);
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
        
        // Table selection
        cardTable.getSelectionModel().addListSelectionListener(e -> updateButtonStates());
    }
    
    private void handleAddCard(ActionEvent e) {
        try {
            if (!mainController.getCardController().canAddMoreCards(Session.getCurrentUser().getUserId())) {
                NotificationManager.showError(this, "Maximum 5 cards allowed per user");
                return;
            }
            
            handleAddCardDialog();
            
        } catch (Exception ex) {
            NotificationManager.showOperationError(this, "add card", ex.getMessage());
        }
    }
    
    private void handleEditCard(ActionEvent e) {
        int selectedRow = cardTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        try {
            Card selectedCard = tableModel.getItemAt(selectedRow);
            String currentName = selectedCard.getCardName();
            
            String newName = JOptionPane.showInputDialog(this, "Enter new card name:", currentName);
            if (newName != null && !newName.trim().isEmpty()) {
                mainController.getCardController().updateCard(selectedCard.getCardId(), 
                    Session.getCurrentUser().getUserId(), newName.trim());
                NotificationManager.showOperationSuccess(this, "Update card");
                refreshData();
            }
            
        } catch (Exception ex) {
            NotificationManager.showOperationError(this, "update card", ex.getMessage());
        }
    }
    
    private void handleToggleStatus(ActionEvent e) {
        int selectedRow = cardTable.getSelectedRow();
        if (selectedRow == -1) {
            NotificationManager.showWarning(this, "Please select a card to toggle status");
            return;
        }
        
        Card selectedCard = tableModel.getItemAt(selectedRow);
        String action = selectedCard.isActive() ? "deactivate" : "activate";
        
        if (NotificationManager.showConfirmation(this, 
            "Are you sure you want to " + action + " this card?")) {
            
            try {
                statusPanel.showProgress("Updating card status...", 50);
                
                selectedCard.setActive(!selectedCard.isActive());
                mainController.getCardController().updateCard(selectedCard.getCardId(), 
                    Session.getCurrentUser().getUserId(), selectedCard.getCardName());
                
                statusPanel.showProgress("Status updated", 100);
                NotificationManager.showOperationSuccess(this, "Update card status");
                refreshData();
                
            } catch (Exception ex) {
                statusPanel.showStatus("Failed to update status", StatusPanel.StatusType.ERROR);
                NotificationManager.showOperationError(this, "update card status", ex.getMessage());
            }
        }
    }
    
    private void handleDeleteCard(ActionEvent e) {
        int selectedRow = cardTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        Card selectedCard = tableModel.getItemAt(selectedRow);
        
        if (ConfirmationDialog.showConfirmation(this, "Delete Card", 
            "Are you sure you want to delete the card '" + selectedCard.getCardName() + "'?", 
            "Delete")) {
            
            try {
                statusPanel.showProgress("Deleting card...", 50);
                mainController.getCardController().deleteCard(selectedCard.getCardId(), 
                    Session.getCurrentUser().getUserId());
                statusPanel.showProgress("Card deleted", 100);
                
                NotificationManager.showOperationSuccess(this, "Delete card");
                refreshData();
                
            } catch (Exception ex) {
                statusPanel.showStatus("Failed to delete card", StatusPanel.StatusType.ERROR);
                NotificationManager.showOperationError(this, "delete card", ex.getMessage());
            }
        }
    }
    
    private void updateButtonStates() {
        boolean hasSelection = cardTable.getSelectedRow() != -1;
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
        toggleStatusButton.setEnabled(hasSelection);
        
        if (hasSelection) {
            Card selectedCard = tableModel.getItemAt(cardTable.getSelectedRow());
            toggleStatusButton.setText(selectedCard.isActive() ? "🔴 Deactivate" : "🟢 Activate");
            statusPanel.showStatus("Card selected: " + selectedCard.getCardName(), 
                                 StatusPanel.StatusType.INFO);
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
                    tableModel.setData(allCards);
                    updateButtonStates();
                    
                    long activeCards = allCards.stream().mapToLong(card -> card.isActive() ? 1 : 0).sum();
                    double totalBalance = allCards.stream().mapToDouble(card -> card.getBalance()).sum();
                    
                    statusPanel.showProgress("Complete", 100);
                    statusPanel.showStatus("Loaded " + allCards.size() + " cards (" + 
                                         activeCards + " active, $" + String.format("%.2f", totalBalance) + " total)", 
                                         StatusPanel.StatusType.SUCCESS);
                    
                } catch (Exception e) {
                    statusPanel.showStatus("Failed to load cards", StatusPanel.StatusType.ERROR);
                    NotificationManager.showOperationError(CardPanel.this, 
                                                         "load cards", e.getMessage());
                }
            }
        };
        
        worker.execute();
    }
    
    private void performSearch() {
        String searchText = searchPanel.getSearchText().toLowerCase();
        
        if (searchText.isEmpty()) {
            tableModel.clearFilter();
            statusPanel.showStatus("Showing all cards", StatusPanel.StatusType.INFO);
            return;
        }
        
        tableModel.filter(card -> {
            return card.getCardName().toLowerCase().contains(searchText) ||
                   card.getCardType().toString().toLowerCase().contains(searchText) ||
                   (card.isActive() ? "active" : "inactive").contains(searchText);
        });
        
        int filteredCount = tableModel.getFilteredData().size();
        statusPanel.showStatus("Found " + filteredCount + " matching cards", 
                             StatusPanel.StatusType.SUCCESS);
    }
    
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
        JTextField numberField = new JTextField(20);
        JComboBox<CardType> typeCombo = new JComboBox<>(CardType.values());
        JTextField balanceField = new JTextField("0.00", 20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Card Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Card Number:"), gbc);
        gbc.gridx = 1;
        formPanel.add(numberField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Card Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(typeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
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
                    NotificationManager.showValidationError(dialog, "Card Name", "Card name is required");
                    return;
                }
                
                if (numberField.getText().trim().length() != 16) {
                    NotificationManager.showValidationError(dialog, "Card Number", "Card number must be 16 digits");
                    return;
                }
                
                double balance = Double.parseDouble(balanceField.getText());
                if (balance < 0) {
                    NotificationManager.showValidationError(dialog, "Balance", "Balance cannot be negative");
                    return;
                }
                
                statusPanel.showProgress("Creating card...", 50);
                
                Card newCard = new Card();
                newCard.setUserId(Session.getCurrentUser().getUserId());
                newCard.setCardName(nameField.getText().trim());
                newCard.setCardNumber(numberField.getText().trim());
                newCard.setCardType((CardType) typeCombo.getSelectedItem());
                newCard.setBalance(balance);
                newCard.setActive(true);
                
                mainController.getCardController().addCard(
                    Session.getCurrentUser().getUserId(),
                    newCard.getCardName(),
                    newCard.getCardType());
                
                statusPanel.showProgress("Card created", 100);
                NotificationManager.showOperationSuccess(this, "Add card");
                
                dialog.dispose();
                refreshData();
                
            } catch (NumberFormatException ex) {
                NotificationManager.showValidationError(dialog, "Balance", "Please enter a valid balance amount");
            } catch (Exception ex) {
                statusPanel.showStatus("Failed to create card", StatusPanel.StatusType.ERROR);
                NotificationManager.showOperationError(dialog, "create card", ex.getMessage());
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