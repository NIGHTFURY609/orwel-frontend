package com.orwel.config;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages SQLite database connections
 * Provides singleton connection and database initialization
 */
public class DatabaseConfig {
    private static final String DB_DIR = System.getProperty("user.home") + File.separator + ".orwel";
    private static final String DB_FILE = DB_DIR + File.separator + "orwel.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;
    private static Connection connection = null;

    /**
     * Get database connection (singleton pattern)
     * Creates database directory and file if they don't exist
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Create directory if it doesn't exist
                File dbDirectory = new File(DB_DIR);
                if (!dbDirectory.exists()) {
                    dbDirectory.mkdirs();
                }

                // Load SQLite JDBC driver
                Class.forName("org.sqlite.JDBC");
                
                // Create connection
                connection = DriverManager.getConnection(DB_URL);
                
                // Enable foreign keys
                connection.createStatement().execute("PRAGMA foreign_keys = ON");
                
                System.out.println("Connected to SQLite database at: " + DB_FILE);
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC driver not found", e);
            }
        }
        return connection;
    }

    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    /**
     * Get database file path
     */
    public static String getDatabasePath() {
        return DB_FILE;
    }

    /**
     * Check if database exists
     */
    public static boolean databaseExists() {
        File dbFile = new File(DB_FILE);
        return dbFile.exists();
    }

    /**
     * Delete database file (for testing purposes)
     */
    public static boolean deleteDatabase() {
        try {
            closeConnection();
            File dbFile = new File(DB_FILE);
            return dbFile.delete();
        } catch (Exception e) {
            System.err.println("Error deleting database: " + e.getMessage());
            return false;
        }
    }
}
