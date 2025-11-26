package com.nfcpay.dao;

import com.nfcpay.model.Wallet;
import com.nfcpay.model.enums.Currency;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

/**
 * Wallet Data Access Object
 * Handles all database operations for wallets table
 * Uses PreparedStatements to prevent SQL injection
 */
public class WalletDAO {
    private DatabaseConnection dbConnection;
    
    public WalletDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // CREATE - Create new wallet for user
    public boolean createWallet(Wallet wallet) {
        String sql = "INSERT INTO wallets (user_id, balance, currency, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, wallet.getUserId());
            pstmt.setBigDecimal(2, wallet.getBalance());
            pstmt.setString(3, wallet.getCurrency().toString());
            pstmt.setTimestamp(4, Timestamp.valueOf(wallet.getCreatedAt()));
            pstmt.setTimestamp(5, Timestamp.valueOf(wallet.getUpdatedAt()));
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    wallet.setWalletId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating wallet: " + e.getMessage());
        }
        return false;
    }
    
    // READ - Get wallet by user ID
    public Wallet getWalletByUserId(int userId) {
        String sql = "SELECT * FROM wallets WHERE user_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToWallet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting wallet by user ID: " + e.getMessage());
        }
        return null;
    }
    

    
    // READ - Get current balance
    public BigDecimal getBalance(int userId) {
        String sql = "SELECT balance FROM wallets WHERE user_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal("balance");
            }
        } catch (SQLException e) {
            System.err.println("Error getting balance: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }
    

    
    // UPDATE - Add funds to wallet
    public boolean addFunds(int userId, BigDecimal amount) {
        String sql = "UPDATE wallets SET balance = balance + ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, amount);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding funds: " + e.getMessage());
        }
        return false;
    }
    
    // UPDATE - Deduct funds from wallet
    public boolean deductFunds(int userId, BigDecimal amount) {
        String sql = "UPDATE wallets SET balance = balance - ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND balance >= ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, amount);
            pstmt.setInt(2, userId);
            pstmt.setBigDecimal(3, amount); // Ensure sufficient balance
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deducting funds: " + e.getMessage());
        }
        return false;
    }
    

    
    // VALIDATION - Check if user has sufficient balance
    public boolean hasSufficientBalance(int userId, BigDecimal amount) {
        String sql = "SELECT balance FROM wallets WHERE user_id = ? AND balance >= ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setBigDecimal(2, amount);
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking sufficient balance: " + e.getMessage());
        }
        return false;
    }
    

    
    // Helper method to map ResultSet to Wallet object
    private Wallet mapResultSetToWallet(ResultSet rs) throws SQLException {
        Wallet wallet = new Wallet();
        wallet.setWalletId(rs.getInt("wallet_id"));
        wallet.setUserId(rs.getInt("user_id"));
        wallet.setBalance(rs.getBigDecimal("balance"));
        wallet.setCurrency(Currency.valueOf(rs.getString("currency")));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            wallet.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            wallet.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return wallet;
    }
}