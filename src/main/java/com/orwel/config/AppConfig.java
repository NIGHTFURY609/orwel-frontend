package com.orwel.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties = new Properties();
    private static Dotenv dotenv;
    
    // Load environment variables from .env file
    static {
        dotenv = loadDotenv();
    }
    
    private static Dotenv loadDotenv() {
        // Try multiple directories to find .env
        String[] searchPaths = {
            System.getProperty("user.dir"),  // Current working directory
            System.getProperty("user.dir") + "/orwel-frontend",
            System.getenv("HOME") + "/Documents/ullivada/orwell/orwel-frontend"
        };
        
        for (String path : searchPaths) {
            try {
                java.io.File envFile = new java.io.File(path, ".env");
                if (envFile.exists()) {
                    System.out.println("Loading .env from: " + path);
                    return Dotenv.configure()
                            .directory(path)
                            .load();
                }
            } catch (Exception e) {
                // Try next path
            }
        }
        
        // Fallback: try default
        try {
            return Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
        } catch (Exception e) {
            System.err.println("Could not load .env file: " + e.getMessage());
            return null;
        }
    }
    
    // Application Configuration
    public static String APP_TITLE = "ORWEL";
    public static String APP_SUBTITLE = "Government Policy Tracker";
    public static String APP_TAGLINE = "Track policies. Stay informed. Make better decisions.";
    public static String APP_DEMO_HINT = "💡 Demo Mode: Enter any username to explore";
    public static String APP_WELCOME_MESSAGE = "Welcome Back!";
    public static String APP_WELCOME_DESCRIPTION = "Track government policies and stay informed about countries worldwide";
    
    // API Configuration
    public static String API_BASE_URL = getEnvOrDefault("BACKEND_API_URL", "http://localhost:8080/api");
    
    // Supabase Configuration
    public static String SUPABASE_URL = getEnvOrDefault("SUPABASE_URL", "");
    public static String SUPABASE_ANON_KEY = getEnvOrDefault("SUPABASE_ANON_KEY", "");
    public static String SUPABASE_SERVICE_ROLE_KEY = getEnvOrDefault("SUPABASE_SERVICE_ROLE_KEY", "");
    
    // Database Configuration
    public static String DATABASE_URL = getEnvOrDefault("DATABASE_URL", "");
    
    // JWT Configuration
    public static String JWT_SECRET = getEnvOrDefault("JWT_SECRET", "change-me-to-random-string");
    public static String JWT_EXPIRATION = getEnvOrDefault("JWT_EXPIRATION", "86400");
    
    // Location API Configuration
    public static String LOCATION_API_KEY = getEnvOrDefault("LOCATION_API_KEY", "");
    public static String LOCATION_API_PROVIDER = "google"; // google, openstreetmap, etc.
    
    // News API Configuration
    public static String NEWS_API_KEY = getEnvOrDefault("NEWS_API_KEY", "");
    
    /**
     * Get environment variable from .env file or system env, with fallback to default
     */
    private static String getEnvOrDefault(String key, String defaultValue) {
        // Try .env file first
        if (dotenv != null) {
            String value = dotenv.get(key);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        
        // Try system environment variable
        String sysEnv = System.getenv(key);
        if (sysEnv != null && !sysEnv.isEmpty()) {
            return sysEnv;
        }
        
        // Use default
        return defaultValue;
    }
    
    public static void loadConfig() {
        try {
            properties.load(new FileInputStream(CONFIG_FILE));
            APP_TITLE = properties.getProperty("app.title", APP_TITLE);
            APP_SUBTITLE = properties.getProperty("app.subtitle", APP_SUBTITLE);
            APP_TAGLINE = properties.getProperty("app.tagline", APP_TAGLINE);
            APP_DEMO_HINT = properties.getProperty("app.demo.hint", APP_DEMO_HINT);
            APP_WELCOME_MESSAGE = properties.getProperty("app.welcome.message", APP_WELCOME_MESSAGE);
            APP_WELCOME_DESCRIPTION = properties.getProperty("app.welcome.description", APP_WELCOME_DESCRIPTION);
            
            // Override with environment variables if present
            API_BASE_URL = getEnvOrDefault("BACKEND_API_URL", 
                    properties.getProperty("api.base.url", API_BASE_URL));
            LOCATION_API_KEY = getEnvOrDefault("LOCATION_API_KEY", 
                    properties.getProperty("location.api.key", LOCATION_API_KEY));
            LOCATION_API_PROVIDER = properties.getProperty("location.api.provider", LOCATION_API_PROVIDER);
            NEWS_API_KEY = getEnvOrDefault("NEWS_API_KEY", 
                    properties.getProperty("news.api.key", NEWS_API_KEY));
        } catch (IOException e) {
            // Config file doesn't exist, use defaults
            System.out.println("Config file not found, using default configuration");
            saveConfig();
        }
    }
    
    public static void saveConfig() {
        try {
            properties.setProperty("app.title", APP_TITLE);
            properties.setProperty("app.subtitle", APP_SUBTITLE);
            properties.setProperty("app.tagline", APP_TAGLINE);
            properties.setProperty("app.demo.hint", APP_DEMO_HINT);
            properties.setProperty("app.welcome.message", APP_WELCOME_MESSAGE);
            properties.setProperty("app.welcome.description", APP_WELCOME_DESCRIPTION);
            properties.setProperty("api.base.url", API_BASE_URL);
            properties.setProperty("location.api.key", LOCATION_API_KEY);
            properties.setProperty("location.api.provider", LOCATION_API_PROVIDER);
            properties.setProperty("news.api.key", NEWS_API_KEY);
            properties.store(new FileOutputStream(CONFIG_FILE), 
                    "Orwel Application Configuration\n" +
                    "Note: Some values may be overridden by .env file");
        } catch (IOException e) {
            System.err.println("Failed to save configuration: " + e.getMessage());
        }
    }
    
    /**
     * Check if Supabase is configured
     */
    public static boolean isSupabaseConfigured() {
        return !SUPABASE_URL.isEmpty() && !SUPABASE_ANON_KEY.isEmpty();
    }
    
    /**
     * Print current configuration (for debugging)
     */
    public static void printConfig() {
        System.out.println("=== Orwel Configuration ===");
        System.out.println("Backend API URL: " + API_BASE_URL);
        System.out.println("Supabase URL: " + (SUPABASE_URL.isEmpty() ? "Not configured" : SUPABASE_URL));
        System.out.println("Supabase Anon Key: " + (SUPABASE_ANON_KEY.isEmpty() ? "Not configured" : "***"));
        System.out.println("Database URL: " + (DATABASE_URL.isEmpty() ? "Not configured" : "***"));
        System.out.println("===========================");
    }
}
