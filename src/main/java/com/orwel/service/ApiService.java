package com.orwel.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orwel.config.AppConfig;
import com.orwel.model.*;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ApiService {
    private static final ApiService instance = new ApiService();
    private final OkHttpClient client;
    private final Gson gson;
    private String authToken;
    private User currentUser;
    
    static {
        // Initialize SQLite database on class load
        try {
            UserDatabase.initializeDatabase();
            System.out.println("SQLite database initialized successfully");
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private ApiService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
        
        // Check if Supabase direct connection is configured
        if (AppConfig.isSupabaseConfigured()) {
            System.out.println("✓ Using direct Supabase connection");
        } else {
            System.out.println("⚠ Supabase not configured - using offline mode only");
            System.out.println("  Add SUPABASE_URL and SUPABASE_ANON_KEY to .env file");
        }
    }
    
    public static ApiService getInstance() {
        return instance;
    }
    
    public void setAuthToken(String token) {
        this.authToken = token;
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    private Request.Builder createRequestBuilder() {
        Request.Builder builder = new Request.Builder();
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }
        builder.addHeader("Content-Type", "application/json");
        return builder;
    }
    
    // Authentication Endpoints
    public AuthResponse login(LoginRequest loginRequest) throws IOException {
        String json = gson.toJson(loginRequest);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/auth/login")
                .post(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                AuthResponse authResponse = gson.fromJson(response.body().string(), AuthResponse.class);
                if (authResponse.isSuccess() && authResponse.getToken() != null) {
                    setAuthToken(authResponse.getToken());
                    
                    // Save JWT token to SQLite
                    // Note: LoginRequest uses username which could be email or username
                    // We'll save token when we have the actual user object
                    try {
                        // Try to find user by username (which might be email)
                        User user = null;
                        String usernameOrEmail = loginRequest.getUsername();
                        
                        if (usernameOrEmail.contains("@")) {
                            user = UserDatabase.getUserByEmail(usernameOrEmail);
                            if (user != null) {
                                UserDatabase.saveJwtToken(usernameOrEmail, authResponse.getToken());
                            }
                        } else {
                            user = UserDatabase.getUserByUsername(usernameOrEmail);
                            if (user != null) {
                                UserDatabase.saveJwtToken(user.getEmail(), authResponse.getToken());
                            }
                        }
                        
                        if (user != null) {
                            setCurrentUser(user);
                        }
                    } catch (SQLException e) {
                        System.err.println("Failed to save token to database: " + e.getMessage());
                    }
                }
                return authResponse;
            } else {
                AuthResponse errorResponse = new AuthResponse();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Login failed: " + response.code());
                return errorResponse;
            }
        }
    }
    
    public AuthResponse register(User user) throws IOException {
        String json = gson.toJson(user);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/auth/register")
                .post(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                AuthResponse authResponse = gson.fromJson(response.body().string(), AuthResponse.class);
                
                // Save user to SQLite for offline access
                if (authResponse.isSuccess()) {
                    try {
                        UserDatabase.saveUser(user);
                        System.out.println("User saved to local database");
                    } catch (SQLException e) {
                        System.err.println("Failed to save user to database: " + e.getMessage());
                    }
                }
                
                return authResponse;
            } else {
                AuthResponse errorResponse = new AuthResponse();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Registration failed: " + response.code());
                return errorResponse;
            }
        }
    }
    
    public void logout() {
        this.authToken = null;
        this.currentUser = null;
    }
    
    // User Endpoints
    public User fetchCurrentUser() throws IOException {
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/users/me")
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                User user = gson.fromJson(response.body().string(), User.class);
                setCurrentUser(user);
                
                // Save to SQLite
                try {
                    UserDatabase.saveUser(user);
                } catch (SQLException e) {
                    System.err.println("Failed to save user to database: " + e.getMessage());
                }
                
                return user;
            }
            return null;
        }
    }
    
    public User updateUser(User user) throws IOException {
        String json = gson.toJson(user);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/users/me")
                .put(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                User updatedUser = gson.fromJson(response.body().string(), User.class);
                setCurrentUser(updatedUser);
                
                // Save to SQLite
                try {
                    UserDatabase.saveUser(updatedUser);
                } catch (SQLException e) {
                    System.err.println("Failed to save user to database: " + e.getMessage());
                }
                
                return updatedUser;
            }
            return null;
        }
    }
    
    // Country Endpoints
    public List<Country> getAllCountries() throws IOException {
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/countries")
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<Country>>(){}.getType();
                return gson.fromJson(response.body().string(), listType);
            }
            return null;
        }
    }
    
    public Country getCountryByCode(String countryCode) throws IOException {
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/countries/" + countryCode)
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return gson.fromJson(response.body().string(), Country.class);
            }
            return null;
        }
    }
    
    public List<Warning> getWarningsForUser(String countryCode) throws IOException {
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/countries/" + countryCode + "/warnings")
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<Warning>>(){}.getType();
                return gson.fromJson(response.body().string(), listType);
            }
            return null;
        }
    }
    
    // News Endpoints
    public List<NewsArticle> getPersonalizedNews() throws IOException {
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/news/personalized")
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<NewsArticle>>(){}.getType();
                return gson.fromJson(response.body().string(), listType);
            }
            return null;
        }
    }
    
    public List<NewsArticle> getGeneralNews() throws IOException {
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/news/general")
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<NewsArticle>>(){}.getType();
                return gson.fromJson(response.body().string(), listType);
            }
            return null;
        }
    }
    
    public List<NewsArticle> getNewsByCountry(String countryCode) throws IOException {
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/news/country/" + countryCode)
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<NewsArticle>>(){}.getType();
                return gson.fromJson(response.body().string(), listType);
            }
            return null;
        }
    }
    
    public List<NewsArticle> getNewsByRegion(String region) throws IOException {
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/news/region/" + region)
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<NewsArticle>>(){}.getType();
                return gson.fromJson(response.body().string(), listType);
            }
            return null;
        }
    }
    
    public List<NewsArticle> searchNews(String query, String countryCode, String region) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(AppConfig.API_BASE_URL + "/news/search").newBuilder();
        urlBuilder.addQueryParameter("q", query);
        if (countryCode != null) urlBuilder.addQueryParameter("country", countryCode);
        if (region != null) urlBuilder.addQueryParameter("region", region);
        
        Request request = createRequestBuilder()
                .url(urlBuilder.build().toString())
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<NewsArticle>>(){}.getType();
                return gson.fromJson(response.body().string(), listType);
            }
            return null;
        }
    }
    
    // User Tags Endpoints
    public void saveUserTags(List<String> tags) throws IOException {
        // Save to backend API
        String json = gson.toJson(tags);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/user/tags")
                .post(body)
                .build();
        
        client.newCall(request).execute().close();
        
        // Save to SQLite
        if (currentUser != null) {
            try {
                UserDatabase.saveCommodityTags(currentUser.getEmail(), tags);
                currentUser.setCommodityTags(tags);
            } catch (SQLException e) {
                System.err.println("Failed to save tags to database: " + e.getMessage());
            }
        }
    }
    
    public List<String> getUserTags() throws IOException {
        // Try to get from backend API first
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/user/tags")
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<String>>(){}.getType();
                List<String> tags = gson.fromJson(response.body().string(), listType);
                
                // Save to SQLite for offline access
                if (currentUser != null && tags != null) {
                    try {
                        UserDatabase.saveCommodityTags(currentUser.getEmail(), tags);
                    } catch (SQLException e) {
                        System.err.println("Failed to save tags to database: " + e.getMessage());
                    }
                }
                
                return tags;
            }
        } catch (IOException e) {
            // If backend fails, try SQLite
            System.out.println("Backend unavailable, loading tags from local database");
            if (currentUser != null) {
                try {
                    return UserDatabase.getCommodityTags(currentUser.getEmail());
                } catch (SQLException sqlEx) {
                    System.err.println("Failed to load tags from database: " + sqlEx.getMessage());
                }
            }
            throw e;
        }
        
        return null;
    }
    
    // Legislation Endpoints
    public List<Legislation> getLegislationByTags(List<String> tags) throws IOException {
        // Try Supabase direct connection first
        if (AppConfig.isSupabaseConfigured()) {
            try {
                return SupabaseClient.getInstance().getLegislationByTags(tags);
            } catch (Exception e) {
                System.err.println("Supabase call failed, falling back to backend: " + e.getMessage());
            }
        }
        
        // Fallback to custom backend API
        String json = gson.toJson(tags);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/legislation/by-tags")
                .post(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<Legislation>>(){}.getType();
                return gson.fromJson(response.body().string(), listType);
            }
            return null;
        }
    }
    
    public Legislation getLegislationById(Integer id) throws IOException {
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/legislation/" + id)
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return gson.fromJson(response.body().string(), Legislation.class);
            }
            return null;
        }
    }
    
    // Committee Hearings Endpoints
    public List<CommitteeMaterial> getHearingsByTags(List<String> tags) throws IOException {
        // Try Supabase direct connection first
        if (AppConfig.isSupabaseConfigured()) {
            try {
                return SupabaseClient.getInstance().getHearingsByTags(tags);
            } catch (Exception e) {
                System.err.println("Supabase hearings call failed, falling back to backend: " + e.getMessage());
            }
        }
        
        String json = gson.toJson(tags);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/hearings/by-tags")
                .post(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<CommitteeMaterial>>(){}.getType();
                return gson.fromJson(response.body().string(), listType);
            }
            return null;
        }
    }
    
    // Nominations Endpoints
    public List<Nomination> getNominationsByTags(List<String> tags) throws IOException {
        // Try Supabase direct connection first
        if (AppConfig.isSupabaseConfigured()) {
            try {
                return SupabaseClient.getInstance().getNominationsByTags(tags);
            } catch (Exception e) {
                System.err.println("Supabase nominations call failed, falling back to backend: " + e.getMessage());
            }
        }
        
        String json = gson.toJson(tags);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/nominations/by-tags")
                .post(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<Nomination>>(){}.getType();
                return gson.fromJson(response.body().string(), listType);
            }
            return null;
        }
    }
    
    // Committees Endpoints
    public List<Committee> getCommitteesByTags(List<String> tags) throws IOException {
        // Try Supabase direct connection first
        if (AppConfig.isSupabaseConfigured()) {
            try {
                return SupabaseClient.getInstance().getCommitteesByTags(tags);
            } catch (Exception e) {
                System.err.println("Supabase committees call failed, falling back to backend: " + e.getMessage());
            }
        }
        
        String json = gson.toJson(tags);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/committees/by-tags")
                .post(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<Committee>>(){}.getType();
                return gson.fromJson(response.body().string(), listType);
            }
            return null;
        }
    }
    
    // Treaties Endpoints
    public List<Treaty> getTreatiesByTags(List<String> tags) throws IOException {
        String json = gson.toJson(tags);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/treaties/by-tags")
                .post(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<Treaty>>(){}.getType();
                return gson.fromJson(response.body().string(), listType);
            }
            return null;
        }
    }
    
    // Research Reports Endpoints
    public List<ResearchReport> getResearchReportsByTags(List<String> tags) throws IOException {
        String json = gson.toJson(tags);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/research-reports/by-tags")
                .post(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<ResearchReport>>(){}.getType();
                return gson.fromJson(response.body().string(), listType);
            }
            return null;
        }
    }
    
    // Dashboard Statistics
    public DashboardStats getDashboardStats(List<String> tags) throws IOException {
        String json = gson.toJson(tags);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/dashboard/stats")
                .post(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return gson.fromJson(response.body().string(), DashboardStats.class);
            }
            return null;
        }
    }
}
