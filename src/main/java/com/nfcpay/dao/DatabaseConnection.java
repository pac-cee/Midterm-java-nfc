package com.nfcpay.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Database Connection Singleton
 * Manages PostgreSQL database connections
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    
    // Database configuration - H2 (file-based)
    private static final String DB_URL = "jdbc:h2:./nfc_payment_system;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    private DatabaseConnection() {
        try {
            Class.forName("org.h2.Driver");
            System.out.println("🔗 Attempting to connect to: " + DB_URL);
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✅ H2 database connection established successfully!");
            
            // Initialize database schema
            initializeSchema();
        } catch (ClassNotFoundException e) {
            System.err.println("❌ H2 Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            System.err.println("   URL: " + DB_URL);
            System.err.println("   User: " + DB_USER);
            e.printStackTrace();
        }
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to get connection: " + e.getMessage());
        }
        return connection;
    }
    
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Database connection test: SUCCESS");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection test: FAILED - " + e.getMessage());
        }
        return false;
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing connection: " + e.getMessage());
        }
    }
    
    private void initializeSchema() {
        try (Connection conn = getConnection()) {
            // Create tables for H2
            String[] createTables = {
                "CREATE TABLE IF NOT EXISTS users (" +
                "user_id IDENTITY PRIMARY KEY," +
                "full_name VARCHAR(100) NOT NULL," +
                "email VARCHAR(100) UNIQUE NOT NULL," +
                "password_hash VARCHAR(255) NOT NULL," +
                "phone VARCHAR(15)," +
                "is_active BOOLEAN DEFAULT TRUE," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "last_login TIMESTAMP)",
                
                "CREATE TABLE IF NOT EXISTS wallets (" +
                "wallet_id IDENTITY PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "balance DECIMAL(10,2) DEFAULT 0.00," +
                "currency VARCHAR(3) DEFAULT 'USD'," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id))",
                
                "CREATE TABLE IF NOT EXISTS cards (" +
                "card_id IDENTITY PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "card_uid VARCHAR(50) UNIQUE NOT NULL," +
                "card_name VARCHAR(50) NOT NULL," +
                "card_type VARCHAR(20) DEFAULT 'VIRTUAL'," +
                "balance DECIMAL(10,2) DEFAULT 0.00," +
                "is_active BOOLEAN DEFAULT TRUE," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id))",
                
                "CREATE TABLE IF NOT EXISTS merchants (" +
                "merchant_id IDENTITY PRIMARY KEY," +
                "merchant_name VARCHAR(100) NOT NULL," +
                "merchant_code VARCHAR(20) UNIQUE NOT NULL," +
                "category VARCHAR(50) DEFAULT 'General'," +
                "is_active BOOLEAN DEFAULT TRUE," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",
                
                "CREATE TABLE IF NOT EXISTS transactions (" +
                "transaction_id IDENTITY PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "card_id INT NOT NULL," +
                "merchant_id INT NOT NULL," +
                "amount DECIMAL(10,2) NOT NULL," +
                "transaction_type VARCHAR(20) DEFAULT 'PAYMENT'," +
                "status VARCHAR(20) DEFAULT 'PENDING'," +
                "reference_code VARCHAR(50) UNIQUE NOT NULL," +
                "description TEXT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "processed_at TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)," +
                "FOREIGN KEY (card_id) REFERENCES cards(card_id)," +
                "FOREIGN KEY (merchant_id) REFERENCES merchants(merchant_id))"
            };
            
            for (String sql : createTables) {
                conn.createStatement().execute(sql);
            }
            
            // Insert sample merchants if none exist
            String checkMerchants = "SELECT COUNT(*) FROM merchants";
            ResultSet rs = conn.createStatement().executeQuery(checkMerchants);
            if (rs.next() && rs.getInt(1) == 0) {
                String[] sampleMerchants = {
                    "INSERT INTO merchants (merchant_name, merchant_code, category) VALUES ('Amazon', 'AMZ001', 'E-commerce')",
                    "INSERT INTO merchants (merchant_name, merchant_code, category) VALUES ('Starbucks', 'SBX001', 'Food & Beverage')",
                    "INSERT INTO merchants (merchant_name, merchant_code, category) VALUES ('Shell Gas', 'SHL001', 'Fuel')",
                    "INSERT INTO merchants (merchant_name, merchant_code, category) VALUES ('Walmart', 'WMT001', 'Retail')",
                    "INSERT INTO merchants (merchant_name, merchant_code, category) VALUES ('McDonald\'s', 'MCD001', 'Fast Food')",
                    "INSERT INTO merchants (merchant_name, merchant_code, category) VALUES ('Target', 'TGT001', 'Retail')",
                    "INSERT INTO merchants (merchant_name, merchant_code, category) VALUES ('Apple Store', 'APL001', 'Electronics')",
                    "INSERT INTO merchants (merchant_name, merchant_code, category) VALUES ('Netflix', 'NFX001', 'Entertainment')",
                    "INSERT INTO merchants (merchant_name, merchant_code, category) VALUES ('Uber', 'UBR001', 'Transportation')",
                    "INSERT INTO merchants (merchant_name, merchant_code, category) VALUES ('Best Buy', 'BBY001', 'Electronics')",
                    "INSERT INTO merchants (merchant_name, merchant_code, category) VALUES ('Costco', 'CST001', 'Wholesale')",
                    "INSERT INTO merchants (merchant_name, merchant_code, category) VALUES ('Home Depot', 'HDT001', 'Home Improvement')"
                };
                
                for (String sql : sampleMerchants) {
                    conn.createStatement().execute(sql);
                }
                System.out.println("✅ Sample merchants added to database!");
            }
            
            System.out.println("✅ H2 database schema initialized successfully!");
        } catch (SQLException e) {
            System.err.println("❌ Error initializing schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}