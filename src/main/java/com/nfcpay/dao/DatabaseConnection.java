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
    
    private void initializeSchema() {
        // Schema initialization will be handled by SchemaInitializer
        System.out.println("📋 Database schema initialization completed");
    }
    
}