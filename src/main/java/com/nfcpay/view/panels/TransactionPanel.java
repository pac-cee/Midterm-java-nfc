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
    private StatusPanel statusPanel;
    private JButton refundButton;
    private JComboBox<String> typeFilter, dateFilter, cardFilter;
    private CustomButton exportBtn;
    private JLabel paginationInfo, pageLabel;
    private JButton prevBtn, nextBtn;
    private List<Transaction> allTransactions;
    private List<Transaction> filteredTransactions;
    private int currentPage = 1;
    private int itemsPerPage = 10;
    private int totalPages = 0;
    
    public TransactionPanel(MainController mainController) {
        this.mainController = mainController;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        String[] columns = {"Date", "Time", "Merchant", "Amount", "Status", "Card", "Receipt"};
        
        tableModel = new ModernTableModel<>(columns, transaction -> new Object[]{
            transaction.getCreatedAt().toLocalDate(),
            transaction.getCreatedAt().toLocalTime().toString().substring(0, 5),
            transaction.getDescription(),
            String.format("$%.2f", transaction.getAmount()),
            transaction.getStatus() + (transaction.getStatus().toString().equals("SUCCESS") ? " ✅" : " ❌"),
            "Visa", // TODO: Get actual card name
            "📄"
        });
        
        transactionTable = new ModernTable(tableModel);
        searchPanel = new SearchPanel("🔍 Search transactions...");

        statusPanel = new StatusPanel();
        refundButton = new CustomButton("💰 Refund", CustomButton.ButtonStyle.DANGER);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.getBackgroundColor());
        
        // Professional header
        JPanel headerPanel = UIUtils.createHeaderPanel("📋 Transaction History");
        
        // Search and filters panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIUtils.getBackgroundColor());
        topPanel.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_SM, UIUtils.SPACING_MD, UIUtils.SPACING_SM, UIUtils.SPACING_MD));
        
        // Search panel on top
        JPanel searchContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchContainer.setBackground(UIUtils.getBackgroundColor());
        searchContainer.add(searchPanel);
        
        // Enhanced filters panel with better alignment
        JPanel filtersPanel = new JPanel(new BorderLayout());
        filtersPanel.setBackground(UIUtils.getBackgroundColor());
        
        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, UIUtils.SPACING_SM, 0));
        leftFilters.setBackground(UIUtils.getBackgroundColor());
        
        JLabel filtersLabel = new JLabel("🔍 Filters:");
        filtersLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        filtersLabel.setForeground(UIUtils.getTextColor());
        
        typeFilter = new JComboBox<>(new String[]{"All Types", "PAYMENT", "REFUND", "TRANSFER"});
        dateFilter = new JComboBox<>(new String[]{"All Time", "Today", "This Week", "This Month"});
        cardFilter = new JComboBox<>(new String[]{"All Cards", "PHYSICAL", "VIRTUAL"});
        
        // Style filter dropdowns
        Dimension filterSize = new Dimension(120, 35);
        typeFilter.setPreferredSize(filterSize);
        dateFilter.setPreferredSize(filterSize);
        cardFilter.setPreferredSize(filterSize);
        
        leftFilters.add(filtersLabel);
        leftFilters.add(typeFilter);
        leftFilters.add(dateFilter);
        leftFilters.add(cardFilter);
        
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIUtils.SPACING_SM, 0));
        rightActions.setBackground(UIUtils.getBackgroundColor());
        
        exportBtn = new CustomButton("📊 Export CSV", CustomButton.ButtonStyle.SECONDARY);
        exportBtn.setPreferredSize(new Dimension(130, 35));
        exportBtn.addActionListener(this::handleExport);
        
        rightActions.add(exportBtn);
        
        filtersPanel.add(leftFilters, BorderLayout.WEST);
        filtersPanel.add(rightActions, BorderLayout.EAST);
        
        topPanel.add(searchContainer, BorderLayout.NORTH);
        topPanel.add(filtersPanel, BorderLayout.CENTER);
        
        // Professional table styling
        transactionTable.setBackground(UIUtils.getSurfaceColor());
        transactionTable.setForeground(UIUtils.getTextColor());
        transactionTable.getTableHeader().setBackground(UIUtils.PRIMARY);
        transactionTable.getTableHeader().setForeground(Color.WHITE);
        transactionTable.getTableHeader().setFont(UIUtils.FONT_BODY);
        transactionTable.setGridColor(new Color(0xe2e8f0));
        transactionTable.setRowHeight(40);
        transactionTable.setSelectionBackground(UIUtils.PRIMARY.brighter());
        transactionTable.setSelectionForeground(Color.WHITE);
        
        // Professional table container
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIUtils.getSurfaceColor());
        scrollPane.setBackground(UIUtils.getSurfaceColor());
        
        JPanel tableContainer = UIUtils.createCard();
        tableContainer.setLayout(new BorderLayout());
        tableContainer.setBackground(UIUtils.getSurfaceColor());
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with pagination
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(UIUtils.getBackgroundColor());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_SM, UIUtils.SPACING_MD, UIUtils.SPACING_SM, UIUtils.SPACING_MD));
        
        // Pagination info
        paginationInfo = new JLabel("Showing 0-0 of 0 transactions");
        paginationInfo.setFont(UIUtils.FONT_SMALL);
        paginationInfo.setForeground(UIUtils.NEUTRAL);
        
        // Enhanced pagination controls
        JPanel paginationControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, UIUtils.SPACING_SM, 0));
        paginationControls.setBackground(UIUtils.getBackgroundColor());
        
        prevBtn = new CustomButton("◀ Previous", CustomButton.ButtonStyle.SECONDARY);
        prevBtn.setPreferredSize(new Dimension(100, 35));
        
        pageLabel = new JLabel("Page 0 of 0");
        pageLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        pageLabel.setForeground(UIUtils.getTextColor());
        pageLabel.setBorder(BorderFactory.createEmptyBorder(0, UIUtils.SPACING_SM, 0, UIUtils.SPACING_SM));
        
        nextBtn = new CustomButton("Next ▶", CustomButton.ButtonStyle.SECONDARY);
        nextBtn.setPreferredSize(new Dimension(100, 35));
        
        prevBtn.addActionListener(e -> previousPage());
        nextBtn.addActionListener(e -> nextPage());
        
        paginationControls.add(prevBtn);
        paginationControls.add(pageLabel);
        paginationControls.add(nextBtn);
        
        bottomPanel.add(paginationInfo, BorderLayout.WEST);
        bottomPanel.add(paginationControls, BorderLayout.EAST);
        
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIUtils.getBackgroundColor());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD));
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(tableContainer, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.PAGE_END);
    }
    
    private void setupEventHandlers() {
        // Search functionality
        searchPanel.addSearchListener(e -> applyFilters());
        searchPanel.addClearListener(e -> {
            searchPanel.clearSearch();
            applyFilters();
            statusPanel.showStatus("Search cleared", StatusPanel.StatusType.INFO);
        });
        
        // Filter functionality
        typeFilter.addActionListener(e -> applyFilters());
        dateFilter.addActionListener(e -> applyFilters());
        cardFilter.addActionListener(e -> applyFilters());
        
        // Refund button
        refundButton.addActionListener(this::handleRefund);
        transactionTable.getSelectionModel().addListSelectionListener(e -> updateButtonStates());
    }
    
    private void applyFilters() {
        if (allTransactions == null) return;
        
        String searchText = searchPanel.getSearchText().toLowerCase();
        String selectedType = (String) typeFilter.getSelectedItem();
        String selectedDate = (String) dateFilter.getSelectedItem();
        String selectedCard = (String) cardFilter.getSelectedItem();
        
        filteredTransactions = allTransactions.stream()
            .filter(transaction -> {
                // Search filter
                boolean matchesSearch = searchText.isEmpty() || 
                    transaction.getDescription().toLowerCase().contains(searchText) ||
                    String.valueOf(transaction.getAmount()).contains(searchText) ||
                    transaction.getTransactionType().toString().toLowerCase().contains(searchText) ||
                    transaction.getStatus().toString().toLowerCase().contains(searchText);
                
                // Type filter
                boolean matchesType = "All Types".equals(selectedType) || 
                    transaction.getTransactionType().toString().equals(selectedType);
                
                // Card filter (based on actual card types)
                boolean matchesCard = "All Cards".equals(selectedCard) || 
                    selectedCard.equals("PHYSICAL") || selectedCard.equals("VIRTUAL");
                
                // Date filter
                boolean matchesDate = "All Time".equals(selectedDate) || 
                    checkDateFilter(transaction, selectedDate);
                
                return matchesSearch && matchesType && matchesCard && matchesDate;
            })
            .collect(java.util.stream.Collectors.toList());
        
        currentPage = 1;
        updatePagination();
        statusPanel.showStatus("Showing " + filteredTransactions.size() + " transactions", 
                             StatusPanel.StatusType.INFO);
    }
    
    private boolean checkDateFilter(Transaction transaction, String dateFilter) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime transactionDate = transaction.getCreatedAt();
        
        switch (dateFilter) {
            case "Today":
                return transactionDate.toLocalDate().equals(now.toLocalDate());
            case "This Week":
                return transactionDate.isAfter(now.minusWeeks(1));
            case "This Month":
                return transactionDate.isAfter(now.minusMonths(1));
            default:
                return true;
        }
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
                    filteredTransactions = new java.util.ArrayList<>(allTransactions);
                    currentPage = 1;
                    updatePagination();
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
    
    private void handleExport(java.awt.event.ActionEvent e) {
        try {
            if (allTransactions == null || allTransactions.isEmpty()) {
                UIUtils.showWarning(this, "No transactions to export");
                return;
            }
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export Transactions");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.endsWith(".csv")) {
                    filePath += ".csv";
                }
                
                // Simple CSV export
                StringBuilder csv = new StringBuilder();
                csv.append("Date,Time,Merchant,Amount,Status,Type\n");
                
                for (Transaction t : allTransactions) {
                    csv.append(String.format("%s,%s,%s,%.2f,%s,%s\n",
                        t.getCreatedAt().toLocalDate(),
                        t.getCreatedAt().toLocalTime().toString().substring(0, 5),
                        t.getDescription(),
                        t.getAmount(),
                        t.getStatus(),
                        t.getTransactionType()
                    ));
                }
                
                java.nio.file.Files.write(java.nio.file.Paths.get(filePath), csv.toString().getBytes());
                UIUtils.showSuccess(this, "Transactions exported to: " + filePath);
            }
            
        } catch (Exception ex) {
            UIUtils.showError(this, "Export failed: " + ex.getMessage());
        }
    }
    
    private void updatePagination() {
        if (filteredTransactions == null) filteredTransactions = allTransactions;
        if (filteredTransactions == null) filteredTransactions = new java.util.ArrayList<>();
        
        totalPages = (int) Math.ceil((double) filteredTransactions.size() / itemsPerPage);
        if (totalPages == 0) totalPages = 1;
        if (currentPage > totalPages) currentPage = totalPages;
        if (currentPage < 1) currentPage = 1;
        
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, filteredTransactions.size());
        
        List<Transaction> pageTransactions = filteredTransactions.subList(startIndex, endIndex);
        tableModel.setData(pageTransactions);
        
        paginationInfo.setText(String.format("Showing %d-%d of %d transactions", 
            startIndex + 1, endIndex, filteredTransactions.size()));
        pageLabel.setText(String.format("Page %d of %d", currentPage, totalPages));
        
        prevBtn.setEnabled(currentPage > 1);
        nextBtn.setEnabled(currentPage < totalPages);
    }
    
    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            updatePagination();
        }
    }
    
    private void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            updatePagination();
        }
    }
    
}