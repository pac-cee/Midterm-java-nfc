package com.nfcpay.view.panels;

import com.nfcpay.controller.MainController;
import com.nfcpay.model.Wallet;
import com.nfcpay.model.Transaction;
import com.nfcpay.util.Session;
import com.nfcpay.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Dashboard Panel - Overview of user account
 */
public class DashboardPanel extends JPanel {
    private final MainController mainController;
    private JLabel balanceLabel;
    private JLabel cardCountLabel;
    private JTable recentTransactionsTable;
    
    public DashboardPanel(MainController mainController) {
        this.mainController = mainController;
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        balanceLabel = new JLabel("$0.00");
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        balanceLabel.setForeground(new Color(40, 167, 69));
        
        cardCountLabel = new JLabel("0 Cards");
        cardCountLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        cardCountLabel.setForeground(Color.WHITE);
        
        recentTransactionsTable = new JTable();
        recentTransactionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        
        JLabel titleLabel = new JLabel("📊 Dashboard");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        statsPanel.setBackground(new Color(33, 37, 41));
        
        // Balance Card
        JPanel balanceCard = createStatsCard("Wallet Balance", balanceLabel);
        
        // Cards Card
        JPanel cardsCard = createStatsCard("Active Cards", cardCountLabel);
        
        statsPanel.add(balanceCard);
        statsPanel.add(cardsCard);
        
        // Recent Transactions with modern dark styling
        JPanel transactionsPanel = new JPanel(new BorderLayout());
        transactionsPanel.setBackground(new Color(33, 37, 41));
        
        JLabel transactionsTitle = new JLabel("Recent Transactions");
        transactionsTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        transactionsTitle.setForeground(Color.WHITE);
        transactionsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Style the table with dark theme
        recentTransactionsTable.getTableHeader().setBackground(new Color(52, 58, 64));
        recentTransactionsTable.getTableHeader().setForeground(Color.WHITE);
        recentTransactionsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        recentTransactionsTable.setBackground(new Color(33, 37, 41));
        recentTransactionsTable.setForeground(Color.WHITE);
        recentTransactionsTable.setGridColor(new Color(52, 58, 64));
        recentTransactionsTable.setRowHeight(35);
        recentTransactionsTable.setSelectionBackground(new Color(52, 58, 64));
        recentTransactionsTable.setSelectionForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(recentTransactionsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(33, 37, 41));
        scrollPane.setBackground(new Color(33, 37, 41));
        
        transactionsPanel.add(transactionsTitle, BorderLayout.NORTH);
        transactionsPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(33, 37, 41));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(transactionsPanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createStatsCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 58, 64)),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        card.setBackground(new Color(52, 58, 64));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(173, 181, 189));
        
        // Update value label colors for dark theme
        valueLabel.setForeground(Color.WHITE);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    public void refreshData() {
        try {
            System.out.println("Refreshing dashboard data...");
            int userId = Session.getCurrentUser().getUserId();
            
            // Get real wallet balance
            try {
                Wallet wallet = mainController.getWalletController().getWallet(userId);
                balanceLabel.setText(String.format("$%.2f", wallet.getBalance()));
            } catch (Exception e) {
                balanceLabel.setText("$0.00");
            }
            
            // Get real card count
            try {
                int cardCount = mainController.getCardController().getUserCards(userId).size();
                cardCountLabel.setText(cardCount + " Cards");
            } catch (Exception e) {
                cardCountLabel.setText("0 Cards");
            }
            
            // Get real recent transactions
            try {
                List<Transaction> transactions = mainController.getPaymentController().getTransactionHistory(userId);
                updateTransactionsTable(transactions.subList(0, Math.min(5, transactions.size())));
            } catch (Exception e) {
                // Show empty table if no transactions
                String[] columns = {"Date", "Description", "Amount", "Status"};
                Object[][] data = {};
                recentTransactionsTable.setModel(new javax.swing.table.DefaultTableModel(data, columns) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                });
            }
            
            System.out.println("Dashboard data refreshed successfully");
            
        } catch (Exception e) {
            System.err.println("Dashboard refresh error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateTransactionsTable(List<Transaction> transactions) {
        String[] columns = {"Date", "Description", "Amount", "Status"};
        Object[][] data = new Object[transactions.size()][4];
        
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            data[i][0] = t.getCreatedAt().toLocalDate();
            data[i][1] = t.getDescription();
            data[i][2] = String.format("$%.2f", t.getAmount());
            data[i][3] = t.getStatus();
        }
        
        recentTransactionsTable.setModel(new javax.swing.table.DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }
}