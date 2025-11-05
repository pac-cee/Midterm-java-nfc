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
    
    // CREATE - Add new merchant
    public boolean createMerchant(Merchant merchant) {
        String sql = "INSERT INTO merchants (merchant_name, merchant_code, category, is_active, created_at) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, merchant.getMerchantName());
            pstmt.setString(2, merchant.getMerchantCode().toUpperCase());
            pstmt.setString(3, merchant.getCategory());
            pstmt.setBoolean(4, merchant.isActive());
            pstmt.setTimestamp(5, Timestamp.valueOf(merchant.getCreatedAt()));
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    merchant.setMerchantId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating merchant: " + e.getMessage());
        }
        return false;
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
    
    // READ - Get merchant by code
    public Merchant getMerchantByCode(String merchantCode) {
        String sql = "SELECT * FROM merchants WHERE merchant_code = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, merchantCode.toUpperCase());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToMerchant(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting merchant by code: " + e.getMessage());
        }
        return null;
    }
    
    // READ - Get all merchants
    public List<Merchant> getAllMerchants() {
        String sql = "SELECT * FROM merchants ORDER BY merchant_name";
        List<Merchant> merchants = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                merchants.add(mapResultSetToMerchant(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all merchants: " + e.getMessage());
        }
        return merchants;
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
    
    // UPDATE - Update merchant information
    public boolean updateMerchant(Merchant merchant) {
        String sql = "UPDATE merchants SET merchant_name = ?, merchant_code = ?, category = ? WHERE merchant_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, merchant.getMerchantName());
            pstmt.setString(2, merchant.getMerchantCode().toUpperCase());
            pstmt.setString(3, merchant.getCategory());
            pstmt.setInt(4, merchant.getMerchantId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating merchant: " + e.getMessage());
        }
        return false;
    }
    
    // UPDATE - Activate merchant
    public boolean activateMerchant(int merchantId) {
        String sql = "UPDATE merchants SET is_active = true WHERE merchant_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, merchantId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error activating merchant: " + e.getMessage());
        }
        return false;
    }
    
    // UPDATE - Deactivate merchant
    public boolean deactivateMerchant(int merchantId) {
        String sql = "UPDATE merchants SET is_active = false WHERE merchant_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, merchantId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deactivating merchant: " + e.getMessage());
        }
        return false;
    }
    
    // DELETE - Delete merchant (rarely used)
    public boolean deleteMerchant(int merchantId) {
        String sql = "DELETE FROM merchants WHERE merchant_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, merchantId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting merchant: " + e.getMessage());
        }
        return false;
    }
    
    // VALIDATION - Check if merchant code exists
    public boolean merchantCodeExists(String merchantCode) {
        String sql = "SELECT COUNT(*) FROM merchants WHERE merchant_code = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, merchantCode.toUpperCase());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking merchant code existence: " + e.getMessage());
        }
        return false;
    }
    
    // VALIDATION - Check if merchant name exists
    public boolean merchantNameExists(String merchantName) {
        String sql = "SELECT COUNT(*) FROM merchants WHERE merchant_name = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, merchantName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking merchant name existence: " + e.getMessage());
        }
        return false;
    }
    
    // ANALYTICS - Get merchant count
    public int getMerchantCount() {
        String sql = "SELECT COUNT(*) FROM merchants WHERE is_active = true";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting merchant count: " + e.getMessage());
        }
        return 0;
    }
    
    // ANALYTICS - Get all categories
    public List<String> getAllCategories() {
        String sql = "SELECT DISTINCT category FROM merchants WHERE is_active = true ORDER BY category";
        List<String> categories = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting categories: " + e.getMessage());
        }
        return categories;
    }
    
    // ANALYTICS - Get merchant count by category
    public Map<String, Integer> getMerchantCountByCategory() {
        String sql = "SELECT category, COUNT(*) as count FROM merchants WHERE is_active = true GROUP BY category ORDER BY category";
        Map<String, Integer> categoryCount = new HashMap<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                categoryCount.put(rs.getString("category"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting merchant count by category: " + e.getMessage());
        }
        return categoryCount;
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