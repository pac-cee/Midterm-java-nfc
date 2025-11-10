package com.nfcpay.view.panels;

import com.nfcpay.controller.MainController;
import com.nfcpay.model.Transaction;
import com.nfcpay.util.Session;
import com.nfcpay.util.UIUtils;
import com.nfcpay.util.NotificationManager;
import com.nfcpay.view.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Transaction History Panel
 */
public class TransactionPanel extends JPanel {
    private final MainController mainController;
    private ModernTable transactionTable;
    private ModernTableModel<Transaction> tableModel;
    private SearchPanel searchPanel;
    private PaginationPanel paginationPanel;
    private StatusPanel statusPanel;
    private JButton refundButton;
    private List<Transaction> allTransactions;
    
    public TransactionPanel(MainController mainController) {
        this.mainController = mainController;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        String[] columns = {"ID", "Date", "Description", "Amount", "Type", "Status"};
        
        tableModel = new ModernTableModel<>(columns, transaction -> new Object[]{
            transaction.getTransactionId(),
            transaction.getCreatedAt().toLocalDate(),
            transaction.getDescription(),
            String.format("$%.2f", transaction.getAmount()),
            transaction.getTransactionType(),
            transaction.getStatus()
        });
        
        transactionTable = new ModernTable(tableModel);
        searchPanel = new SearchPanel("Search by description, amount, or status...");
        paginationPanel = new PaginationPanel();
        statusPanel = new StatusPanel();
        refundButton = new CustomButton("💰 Request Refund", CustomButton.ButtonStyle.DANGER);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(33, 37, 41));
        
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
        
        JLabel titleLabel = new JLabel("💳 Transaction History");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        
        CustomButton refreshButton = new CustomButton("🔄 Refresh", CustomButton.ButtonStyle.SUCCESS);
        refreshButton.addActionListener(e -> refreshData());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        // Search container
        JPanel searchContainer = new JPanel(new BorderLayout());
        searchContainer.setBackground(new Color(33, 37, 41));
        searchContainer.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        searchContainer.add(searchPanel, BorderLayout.CENTER);
        
        // Style table for dark theme
        transactionTable.setBackground(new Color(33, 37, 41));
        transactionTable.setForeground(Color.WHITE);
        transactionTable.getTableHeader().setBackground(new Color(52, 58, 64));
        transactionTable.getTableHeader().setForeground(Color.WHITE);
        transactionTable.setGridColor(new Color(52, 58, 64));
        transactionTable.setSelectionBackground(new Color(52, 58, 64));
        transactionTable.setSelectionForeground(Color.WHITE);
        
        // Table container
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(33, 37, 41));
        scrollPane.setBackground(new Color(33, 37, 41));
        
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(new Color(33, 37, 41));
        tableContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        // Action panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setBackground(new Color(33, 37, 41));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        actionPanel.add(refundButton);
        
        // Pagination container
        JPanel paginationContainer = new JPanel(new BorderLayout());
        paginationContainer.setBackground(new Color(33, 37, 41));
        paginationContainer.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        paginationContainer.add(paginationPanel, BorderLayout.CENTER);
        
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(searchContainer, BorderLayout.NORTH);
        contentPanel.add(tableContainer, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(actionPanel, BorderLayout.NORTH);
        bottomPanel.add(paginationContainer, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.PAGE_END);
    }
    
    private void setupEventHandlers() {
        // Search functionality
        searchPanel.addSearchListener(e -> performSearch());
        searchPanel.addClearListener(e -> {
            searchPanel.clearSearch();
            tableModel.clearFilter();
            statusPanel.showStatus("Filter cleared", StatusPanel.StatusType.INFO);
        });
        
        // Pagination
        paginationPanel.addNavigationListeners(
            e -> paginationPanel.goToFirstPage(),
            e -> paginationPanel.goToPreviousPage(),
            e -> paginationPanel.goToNextPage(),
            e -> paginationPanel.goToLastPage()
        );
        
        // Refund button
        refundButton.addActionListener(this::handleRefund);
        transactionTable.getSelectionModel().addListSelectionListener(e -> updateButtonStates());
    }
    
    private void performSearch() {
        String searchText = searchPanel.getSearchText().toLowerCase();
        
        if (searchText.isEmpty()) {
            tableModel.clearFilter();
            statusPanel.showStatus("Showing all transactions", StatusPanel.StatusType.INFO);
            return;
        }
        
        tableModel.filter(transaction -> {
            return transaction.getDescription().toLowerCase().contains(searchText) ||
                   String.valueOf(transaction.getAmount()).contains(searchText) ||
                   transaction.getTransactionType().toString().toLowerCase().contains(searchText) ||
                   transaction.getStatus().toString().toLowerCase().contains(searchText);
        });
        
        int filteredCount = tableModel.getFilteredData().size();
        statusPanel.showStatus("Found " + filteredCount + " matching transactions", 
                             StatusPanel.StatusType.INFO);
    }
    
    private void handleRefund(ActionEvent e) {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            NotificationManager.showWarning(this, "Please select a transaction to refund");
            return;
        }
        
        try {
            Transaction selectedTransaction = tableModel.getItemAt(selectedRow);
            String status = selectedTransaction.getStatus().toString();
            String type = selectedTransaction.getTransactionType().toString();
            
            if (!"PAYMENT".equals(type)) {
                NotificationManager.showError(this, "Only payment transactions can be refunded");
                return;
            }
            
            if (!"SUCCESS".equals(status)) {
                NotificationManager.showError(this, "Only successful transactions can be refunded");
                return;
            }
            
            String reason = NotificationManager.showInput(this, 
                "Enter refund reason (optional):", "");
            
            if (reason != null) { // User didn't cancel
                if (NotificationManager.showConfirmation(this, 
                    "Are you sure you want to request a refund for $" + 
                    String.format("%.2f", selectedTransaction.getAmount()) + "?")) {
                    
                    statusPanel.showProgress("Processing refund...", 50);
                    
                    Transaction refundTransaction = mainController.getPaymentController()
                        .refundPayment(selectedTransaction.getTransactionId(), Session.getCurrentUser().getUserId(), reason);
                    
                    statusPanel.showProgress("Refund completed", 100);
                    
                    String successMessage = String.format(
                        "Refund Processed Successfully!\n\n" +
                        "Refund Transaction ID: %d\n" +
                        "Amount Refunded: $%.2f",
                        refundTransaction.getTransactionId(),
                        refundTransaction.getAmount()
                    );
                    
                    NotificationManager.showSuccess(this, successMessage);
                    refreshData();
                }
            }
            
        } catch (Exception ex) {
            NotificationManager.showOperationError(this, "process refund", ex.getMessage());
        }
    }
    
    private void updateButtonStates() {
        boolean hasSelection = transactionTable.getSelectedRow() != -1;
        refundButton.setEnabled(hasSelection);
        
        if (hasSelection) {
            Transaction selectedTransaction = tableModel.getItemAt(transactionTable.getSelectedRow());
            statusPanel.showStatus("Transaction selected: " + selectedTransaction.getDescription(), 
                                 StatusPanel.StatusType.INFO);
        }
    }
    
    public void refreshData() {
        statusPanel.showProgress("Loading transactions...", 0);
        
        SwingWorker<List<Transaction>, Void> worker = new SwingWorker<List<Transaction>, Void>() {
            @Override
            protected List<Transaction> doInBackground() throws Exception {
                statusPanel.showProgress("Loading transactions...", 50);
                return mainController.getPaymentController()
                    .getTransactionHistory(Session.getCurrentUser().getUserId());
            }
            
            @Override
            protected void done() {
                try {
                    allTransactions = get();
                    tableModel.setData(allTransactions);
                    paginationPanel.updatePagination(allTransactions.size());
                    updateButtonStates();
                    
                    statusPanel.showProgress("Transactions loaded", 100);
                    statusPanel.showStatus("Loaded " + allTransactions.size() + " transactions", 
                                         StatusPanel.StatusType.SUCCESS);
                    
                } catch (Exception e) {
                    statusPanel.showStatus("Failed to load transactions", StatusPanel.StatusType.ERROR);
                    NotificationManager.showOperationError(TransactionPanel.this, "load transactions", e.getMessage());
                }
            }
        };
        
        worker.execute();
    }
    

}