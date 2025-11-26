package com.nfcpay.dao;

import com.nfcpay.model.Merchant;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Merchant Data Access Object
 * Handles all database operations for merchants table
 * Uses PreparedStatements to prevent SQL injection
 */
public class MerchantDAO {
    private DatabaseConnection dbConnection;
    
    public MerchantDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    

    
    // READ - Get merchant by ID
    public Merchant getMerchantById(int merchantId) {
        String sql = "SELECT * FROM merchants WHERE merchant_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, merchantId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToMerchant(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting merchant by ID: " + e.getMessage());
        }
        return null;
    }
    

    

    
    // READ - Get active merchants only
    public List<Merchant> getActiveMerchants() {
        String sql = "SELECT * FROM merchants WHERE is_active = true ORDER BY merchant_name";
        List<Merchant> merchants = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                merchants.add(mapResultSetToMerchant(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting active merchants: " + e.getMessage());
        }
        return merchants;
    }
    
    // READ - Get merchants by category
    public List<Merchant> getMerchantsByCategory(String category) {
        String sql = "SELECT * FROM merchants WHERE category = ? AND is_active = true ORDER BY merchant_name";
        List<Merchant> merchants = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                merchants.add(mapResultSetToMerchant(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting merchants by category: " + e.getMessage());
        }
        return merchants;
    }
    

    

    

    
    // Helper method to map ResultSet to Merchant object
    private Merchant mapResultSetToMerchant(ResultSet rs) throws SQLException {
        Merchant merchant = new Merchant();
        merchant.setMerchantId(rs.getInt("merchant_id"));
        merchant.setMerchantName(rs.getString("merchant_name"));
        merchant.setMerchantCode(rs.getString("merchant_code"));
        merchant.setCategory(rs.getString("category"));
        merchant.setActive(rs.getBoolean("is_active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            merchant.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return merchant;
    }
}