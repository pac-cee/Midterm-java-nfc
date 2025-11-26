package com.nfcpay.dao;

import com.nfcpay.model.Transaction;
import com.nfcpay.model.enums.TransactionType;
import com.nfcpay.model.enums.TransactionStatus;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Transaction Data Access Object
 * Handles all database operations for transactions table
 * Uses PreparedStatements to prevent SQL injection
 */
public class TransactionDAO {
    private DatabaseConnection dbConnection;
    
    public TransactionDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // CREATE - Create new transaction
    public boolean createTransaction(Transaction transaction) {
        // Ensure reference code is not null
        if (transaction.getReferenceCode() == null) {
            transaction.setReferenceCode(com.nfcpay.util.UIDGenerator.generateTransactionReference());
        }
        
        String sql = "INSERT INTO transactions (user_id, card_id, merchant_id, amount, transaction_type, status, reference_code, description, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, transaction.getUserId());
            pstmt.setInt(2, transaction.getCardId());
            pstmt.setInt(3, transaction.getMerchantId());
            pstmt.setBigDecimal(4, transaction.getAmount());
            pstmt.setString(5, transaction.getTransactionType().toString());
            pstmt.setString(6, transaction.getStatus().toString());
            pstmt.setString(7, transaction.getReferenceCode());
            pstmt.setString(8, transaction.getDescription());
            pstmt.setTimestamp(9, Timestamp.valueOf(transaction.getCreatedAt()));
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    transaction.setTransactionId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating transaction: " + e.getMessage());
        }
        return false;
    }
    
    // READ - Get transaction by ID
    public Transaction getTransactionById(int transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, transactionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToTransaction(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting transaction by ID: " + e.getMessage());
        }
        return null;
    }
    
    // READ - Get all transactions for user
    public List<Transaction> getTransactionsByUserId(int userId) {
        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY created_at DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting transactions by user ID: " + e.getMessage());
        }
        return transactions;
    }
    
    // READ - Get recent transactions (for dashboard)
    public List<Transaction> getRecentTransactions(int userId, int limit) {
        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting recent transactions: " + e.getMessage());
        }
        return transactions;
    }
    

    

    

    
    // ANALYTICS - Get daily spending for user
    public BigDecimal getDailySpent(int userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND status = 'SUCCESS' AND DATE(created_at) = CURRENT_DATE";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting daily spent: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }
    

    
    // Helper method to map ResultSet to Transaction object
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setUserId(rs.getInt("user_id"));
        transaction.setCardId(rs.getInt("card_id"));
        transaction.setMerchantId(rs.getInt("merchant_id"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setTransactionType(TransactionType.valueOf(rs.getString("transaction_type")));
        transaction.setStatus(TransactionStatus.valueOf(rs.getString("status")));
        transaction.setReferenceCode(rs.getString("reference_code"));
        transaction.setDescription(rs.getString("description"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            transaction.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp processedAt = rs.getTimestamp("processed_at");
        if (processedAt != null) {
            transaction.setProcessedAt(processedAt.toLocalDateTime());
        }
        
        return transaction;
    }
}