package com.orwel.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties = new Properties();
    
    // Application Configuration
    public static String APP_TITLE = "ORWEL";
    public static String APP_SUBTITLE = "Government Policy Tracker";
    public static String APP_TAGLINE = "Track policies. Stay informed. Make better decisions.";
    public static String APP_DEMO_HINT = "💡 Demo Mode: Enter any username to explore";
    public static String APP_WELCOME_MESSAGE = "Welcome Back!";
    public static String APP_WELCOME_DESCRIPTION = "Track government policies and stay informed about countries worldwide";
    
    // API Configuration
    public static String API_BASE_URL = "http://localhost:8080/api";
    
    // Location API Configuration
    public static String LOCATION_API_KEY = ""; // Set your API key here
    public static String LOCATION_API_PROVIDER = "google"; // google, openstreetmap, etc.
    
    // News API Configuration
    public static String NEWS_API_KEY = ""; // Set your news API key if needed
    
    public static void loadConfig() {
        try {
            properties.load(new FileInputStream(CONFIG_FILE));
            APP_TITLE = properties.getProperty("app.title", APP_TITLE);
            APP_SUBTITLE = properties.getProperty("app.subtitle", APP_SUBTITLE);
            APP_TAGLINE = properties.getProperty("app.tagline", APP_TAGLINE);
            APP_DEMO_HINT = properties.getProperty("app.demo.hint", APP_DEMO_HINT);
            APP_WELCOME_MESSAGE = properties.getProperty("app.welcome.message", APP_WELCOME_MESSAGE);
            APP_WELCOME_DESCRIPTION = properties.getProperty("app.welcome.description", APP_WELCOME_DESCRIPTION);
            API_BASE_URL = properties.getProperty("api.base.url", API_BASE_URL);
            LOCATION_API_KEY = properties.getProperty("location.api.key", LOCATION_API_KEY);
            LOCATION_API_PROVIDER = properties.getProperty("location.api.provider", LOCATION_API_PROVIDER);
            NEWS_API_KEY = properties.getProperty("news.api.key", NEWS_API_KEY);
        } catch (IOException e) {
            // Config file doesn't exist, use defaults
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
            properties.store(new FileOutputStream(CONFIG_FILE), "Orwel Application Configuration");
        } catch (IOException e) {
            System.err.println("Failed to save configuration: " + e.getMessage());
        }
    }
}
