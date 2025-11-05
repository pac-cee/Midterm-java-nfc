package com.nfcpay.dao;

import java.sql.Connection;
import java.sql.DriverManager;
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
    
    // Database configuration
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/nfc_payment_system_db";
    private static final String DB_USER = "paccee";
    private static final String DB_PASSWORD = "Euqificap12.";
    
    private DatabaseConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("🔗 Attempting to connect to: " + DB_URL);
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✅ Database connection established successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ PostgreSQL Driver not found: " + e.getMessage());
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
}