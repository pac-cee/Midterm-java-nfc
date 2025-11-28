package com.nfcpay.view.panels;

import com.nfcpay.controller.MainController;
import com.nfcpay.model.Wallet;
import com.nfcpay.model.Transaction;
import com.nfcpay.util.Session;
import com.nfcpay.util.UIUtils;
import com.nfcpay.view.components.CustomButton;

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
    private JLabel paymentsLabel;
    private JTable recentTransactionsTable;
    
    public DashboardPanel(MainController mainController) {
        this.mainController = mainController;
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        balanceLabel = new JLabel("$0.00");
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        
        cardCountLabel = new JLabel("0 Cards");
        cardCountLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        
        recentTransactionsTable = new JTable();
        recentTransactionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recentTransactionsTable.setFont(UIUtils.FONT_BODY);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.getBackgroundColor());
        
        // Professional header
        JPanel headerPanel = UIUtils.createHeaderPanel("📊 Dashboard");
        
        // Add refresh button to header
        JButton refreshButton = new CustomButton("🔄 Refresh", CustomButton.ButtonStyle.SECONDARY);
        refreshButton.addActionListener(e -> refreshData());
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        // Professional Stats Panel - 3 cards in a row
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, UIUtils.SPACING_SM, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_MD, 0, UIUtils.SPACING_MD, 0));
        statsPanel.setBackground(UIUtils.getBackgroundColor());
        
        // Create professional stat cards
        paymentsLabel = new JLabel("0 Payments");
        paymentsLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        
        JPanel balanceCard = createProfessionalStatsCard("💰 Wallet Balance", balanceLabel, UIUtils.SUCCESS);
        JPanel cardsCard = createProfessionalStatsCard("💳 Active Cards", cardCountLabel, UIUtils.PRIMARY);
        JPanel paymentsCard = createProfessionalStatsCard("📈 This Month", paymentsLabel, UIUtils.WARNING);
        
        statsPanel.add(balanceCard);
        statsPanel.add(cardsCard);
        statsPanel.add(paymentsCard);
        
        // Professional Transactions Panel
        JPanel transactionsPanel = UIUtils.createCard();
        transactionsPanel.setLayout(new BorderLayout());
        transactionsPanel.setBackground(UIUtils.getSurfaceColor());
        
        JLabel transactionsTitle = new JLabel("Recent Transactions");
        transactionsTitle.setFont(UIUtils.FONT_HEADING);
        transactionsTitle.setForeground(UIUtils.getTextColor());
        transactionsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, UIUtils.SPACING_SM, 0));
        
        // Professional table styling
        recentTransactionsTable.getTableHeader().setBackground(UIUtils.PRIMARY);
        recentTransactionsTable.getTableHeader().setForeground(Color.WHITE);
        recentTransactionsTable.getTableHeader().setFont(UIUtils.FONT_BODY);
        recentTransactionsTable.setBackground(UIUtils.getSurfaceColor());
        recentTransactionsTable.setForeground(UIUtils.getTextColor());
        recentTransactionsTable.setGridColor(new Color(0xe2e8f0));
        recentTransactionsTable.setRowHeight(40);
        recentTransactionsTable.setSelectionBackground(UIUtils.PRIMARY.brighter());
        recentTransactionsTable.setSelectionForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(recentTransactionsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(33, 37, 41));
        scrollPane.setBackground(new Color(33, 37, 41));
        
        transactionsPanel.add(transactionsTitle, BorderLayout.NORTH);
        transactionsPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIUtils.getBackgroundColor());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD, UIUtils.SPACING_MD));
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(transactionsPanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createProfessionalStatsCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = UIUtils.createCard();
        card.setLayout(new BorderLayout());
        card.setBackground(UIUtils.getSurfaceColor());
        
        // Add colored accent bar at top
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accentColor);
        accentBar.setPreferredSize(new Dimension(0, 4));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIUtils.FONT_BODY);
        titleLabel.setForeground(UIUtils.NEUTRAL);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, UIUtils.SPACING_XS, 0));
        
        // Style value label
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        valueLabel.setForeground(accentColor);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIUtils.getSurfaceColor());
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(valueLabel, BorderLayout.CENTER);
        
        card.add(accentBar, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        
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
            
            // Get real recent transactions and count this month's payments
            try {
                List<Transaction> transactions = mainController.getPaymentController().getTransactionHistory(userId);
                updateTransactionsTable(transactions.subList(0, Math.min(5, transactions.size())));
                
                // Count this month's payments
                long thisMonthPayments = transactions.stream()
                    .filter(t -> t.getCreatedAt().getMonth() == java.time.LocalDateTime.now().getMonth())
                    .count();
                paymentsLabel.setText(thisMonthPayments + " Payments");
                
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
                paymentsLabel.setText("0 Payments");
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