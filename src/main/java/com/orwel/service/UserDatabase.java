package com.orwel.service;

import com.orwel.config.DatabaseConfig;
import com.orwel.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SQLite database service for local user data storage
 * Handles user credentials and commodity tags
 */
public class UserDatabase {
    
    /**
     * Initialize database tables
     * Creates users and commodity_tags tables if they don't exist
     */
    public static void initializeDatabase() throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        
        // Create users table
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                email TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                first_name TEXT,
                last_name TEXT,
                occupation TEXT,
                has_stocks INTEGER DEFAULT 0,
                jwt_token TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        // Create commodity_tags table
        String createTagsTable = """
            CREATE TABLE IF NOT EXISTS commodity_tags (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                tag_name TEXT NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                UNIQUE(user_id, tag_name)
            )
        """;
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createTagsTable);
            System.out.println("Database tables initialized successfully");
        }
        
        // Create demo user if it doesn't exist
        createDemoUserIfNotExists();
    }
    
    /**
     * Create a demo user for testing/demo purposes
     */
    private static void createDemoUserIfNotExists() throws SQLException {
        if (!userExists("demo@orwel.com")) {
            System.out.println("Creating demo user...");
            User demoUser = new User();
            demoUser.setUsername("demo");
            demoUser.setEmail("demo@orwel.com");
            demoUser.setPassword("demo123");
            demoUser.setFirstName("Demo");
            demoUser.setLastName("User");
            demoUser.setOccupation("Trader");
            demoUser.setHasStocks(true);
            
            saveUser(demoUser);
            
            // Add some demo commodity tags
            List<String> demoTags = Arrays.asList("oil", "gold", "technology", "agriculture");
            saveCommodityTags("demo@orwel.com", demoTags);
            
            System.out.println("========================================");
            System.out.println("Demo user created successfully!");
            System.out.println("Email: demo@orwel.com");
            System.out.println("Password: demo123");
            System.out.println("========================================");
        } else {
            System.out.println("Demo user already exists");
        }
    }
    
    /**
     * Save or update user in database
     */
    public static void saveUser(User user) throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        
        String sql = """
            INSERT INTO users (username, email, password, first_name, last_name, occupation, has_stocks, jwt_token)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(email) DO UPDATE SET
                username = excluded.username,
                password = excluded.password,
                first_name = excluded.first_name,
                last_name = excluded.last_name,
                occupation = excluded.occupation,
                has_stocks = excluded.has_stocks,
                jwt_token = excluded.jwt_token
        """;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getFirstName());
            pstmt.setString(5, user.getLastName());
            pstmt.setString(6, user.getOccupation());
            pstmt.setInt(7, (user.getHasStocks() != null && user.getHasStocks()) ? 1 : 0);
            pstmt.setString(8, null); // JWT token will be set separately
            
            pstmt.executeUpdate();
            System.out.println("User saved to database: " + user.getEmail());
        }
        
        // Save commodity tags
        if (user.getCommodityTags() != null && !user.getCommodityTags().isEmpty()) {
            saveCommodityTags(user.getEmail(), user.getCommodityTags());
        }
    }
    
    /**
     * Get user by email
     */
    public static User getUserByEmail(String email) throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setOccupation(rs.getString("occupation"));
                user.setHasStocks(rs.getInt("has_stocks") == 1);
                
                // Load commodity tags
                user.setCommodityTags(getCommodityTags(email));
                
                return user;
            }
        }
        
        return null;
    }
    
    /**
     * Get user by username
     */
    public static User getUserByUsername(String username) throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setOccupation(rs.getString("occupation"));
                user.setHasStocks(rs.getInt("has_stocks") == 1);
                
                // Load commodity tags
                user.setCommodityTags(getCommodityTags(user.getEmail()));
                
                return user;
            }
        }
        
        return null;
    }
    
    /**
     * Save JWT token for user
     */
    public static void saveJwtToken(String email, String token) throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        
        String sql = "UPDATE users SET jwt_token = ? WHERE email = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, token);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Get JWT token for user
     */
    public static String getJwtToken(String email) throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        
        String sql = "SELECT jwt_token FROM users WHERE email = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("jwt_token");
            }
        }
        
        return null;
    }
    
    /**
     * Save commodity tags for user
     */
    public static void saveCommodityTags(String email, List<String> tags) throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        
        // First get user ID
        Long userId = getUserIdByEmail(email);
        if (userId == null) {
            throw new SQLException("User not found: " + email);
        }
        
        // Delete existing tags
        String deleteSql = "DELETE FROM commodity_tags WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setLong(1, userId);
            pstmt.executeUpdate();
        }
        
        // Insert new tags
        String insertSql = "INSERT INTO commodity_tags (user_id, tag_name) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            for (String tag : tags) {
                if (tag != null && !tag.trim().isEmpty()) {
                    pstmt.setLong(1, userId);
                    pstmt.setString(2, tag.trim());
                    pstmt.executeUpdate();
                }
            }
        }
        
        System.out.println("Saved " + tags.size() + " commodity tags for user: " + email);
    }
    
    /**
     * Get commodity tags for user
     */
    public static List<String> getCommodityTags(String email) throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        List<String> tags = new ArrayList<>();
        
        String sql = """
            SELECT ct.tag_name 
            FROM commodity_tags ct
            JOIN users u ON ct.user_id = u.id
            WHERE u.email = ?
            ORDER BY ct.tag_name
        """;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tags.add(rs.getString("tag_name"));
            }
        }
        
        return tags;
    }
    
    /**
     * Get user ID by email
     */
    private static Long getUserIdByEmail(String email) throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        
        String sql = "SELECT id FROM users WHERE email = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getLong("id");
            }
        }
        
        return null;
    }
    
    /**
     * Check if user exists by email
     */
    public static boolean userExists(String email) throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        
        return false;
    }
    
    /**
     * Delete user by email
     */
    public static void deleteUser(String email) throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        
        String sql = "DELETE FROM users WHERE email = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.executeUpdate();
            System.out.println("User deleted: " + email);
        }
    }
    
    /**
     * Get all users
     */
    public static List<User> getAllUsers() throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        List<User> users = new ArrayList<>();
        
        String sql = "SELECT * FROM users";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setOccupation(rs.getString("occupation"));
                user.setHasStocks(rs.getInt("has_stocks") == 1);
                
                // Load commodity tags
                user.setCommodityTags(getCommodityTags(user.getEmail()));
                
                users.add(user);
            }
        }
        
        return users;
    }
}
