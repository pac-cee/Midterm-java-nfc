package com.nfcpay.dao;

import com.nfcpay.model.Card;
import com.nfcpay.model.enums.CardType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Card Data Access Object
 * Handles all database operations for cards table
 * Uses PreparedStatements to prevent SQL injection
 */
public class CardDAO {
    private DatabaseConnection dbConnection;
    
    public CardDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // CREATE - Add new card
    public boolean createCard(Card card) {
        String sql = "INSERT INTO cards (user_id, card_uid, card_name, card_type, is_active, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, card.getUserId());
            pstmt.setString(2, card.getCardUid());
            pstmt.setString(3, card.getCardName());
            pstmt.setString(4, card.getCardType().toString());
            pstmt.setBoolean(5, card.isActive());
            pstmt.setTimestamp(6, Timestamp.valueOf(card.getCreatedAt()));
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    card.setCardId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating card: " + e.getMessage());
        }
        return false;
    }
    
    // READ - Get card by ID
    public Card getCardById(int cardId) {
        String sql = "SELECT * FROM cards WHERE card_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cardId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCard(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting card by ID: " + e.getMessage());
        }
        return null;
    }
    
    // READ - Get card by UID
    public Card getCardByUid(String cardUid) {
        String sql = "SELECT * FROM cards WHERE card_uid = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cardUid);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCard(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting card by UID: " + e.getMessage());
        }
        return null;
    }
    
    // READ - Get all cards for user
    public List<Card> getCardsByUserId(int userId) {
        String sql = "SELECT * FROM cards WHERE user_id = ? ORDER BY created_at DESC";
        List<Card> cards = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                cards.add(mapResultSetToCard(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting cards by user ID: " + e.getMessage());
        }
        return cards;
    }
    
    // READ - Get active cards for user
    public List<Card> getActiveCardsByUserId(int userId) {
        String sql = "SELECT * FROM cards WHERE user_id = ? AND is_active = true ORDER BY created_at DESC";
        List<Card> cards = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                cards.add(mapResultSetToCard(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting active cards: " + e.getMessage());
        }
        return cards;
    }
    
    // UPDATE - Update card information
    public boolean updateCard(Card card) {
        String sql = "UPDATE cards SET card_name = ?, card_type = ? WHERE card_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, card.getCardName());
            pstmt.setString(2, card.getCardType().toString());
            pstmt.setInt(3, card.getCardId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating card: " + e.getMessage());
        }
        return false;
    }
    
    // UPDATE - Activate card
    public boolean activateCard(int cardId) {
        String sql = "UPDATE cards SET is_active = true WHERE card_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cardId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error activating card: " + e.getMessage());
        }
        return false;
    }
    
    // UPDATE - Deactivate card
    public boolean deactivateCard(int cardId) {
        String sql = "UPDATE cards SET is_active = false WHERE card_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cardId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deactivating card: " + e.getMessage());
        }
        return false;
    }
    
    // DELETE - Delete card
    public boolean deleteCard(int cardId) {
        String sql = "DELETE FROM cards WHERE card_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cardId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting card: " + e.getMessage());
        }
        return false;
    }
    
    // VALIDATION - Check if card UID exists
    public boolean cardUidExists(String cardUid) {
        String sql = "SELECT COUNT(*) FROM cards WHERE card_uid = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cardUid);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking card UID existence: " + e.getMessage());
        }
        return false;
    }
    
    // VALIDATION - Check if card belongs to user
    public boolean cardBelongsToUser(int cardId, int userId) {
        String sql = "SELECT COUNT(*) FROM cards WHERE card_id = ? AND user_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cardId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking card ownership: " + e.getMessage());
        }
        return false;
    }
    
    // VALIDATION - Get active card count for user
    public int getActiveCardCount(int userId) {
        String sql = "SELECT COUNT(*) FROM cards WHERE user_id = ? AND is_active = true";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting active card count: " + e.getMessage());
        }
        return 0;
    }
    
    // VALIDATION - Check if user can add more cards (max 5)
    public boolean canAddMoreCards(int userId) {
        return getActiveCardCount(userId) < 5;
    }
    
    // Helper method to map ResultSet to Card object
    private Card mapResultSetToCard(ResultSet rs) throws SQLException {
        Card card = new Card();
        card.setCardId(rs.getInt("card_id"));
        card.setUserId(rs.getInt("user_id"));
        card.setCardUid(rs.getString("card_uid"));
        card.setCardName(rs.getString("card_name"));
        card.setCardType(CardType.valueOf(rs.getString("card_type")));
        card.setActive(rs.getBoolean("is_active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            card.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return card;
    }
}