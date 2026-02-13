package com.orwel.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orwel.config.AppConfig;
import com.orwel.model.*;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ApiService {
    private static final ApiService instance = new ApiService();
    private final OkHttpClient client;
    private final Gson gson;
    private String authToken;
    
    private ApiService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
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
                return gson.fromJson(response.body().string(), AuthResponse.class);
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
    }
    
    // User Endpoints
    public User getCurrentUser() throws IOException {
        Request request = createRequestBuilder()
                .url(AppConfig.API_BASE_URL + "/users/me")
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return gson.fromJson(response.body().string(), User.class);
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
                return gson.fromJson(response.body().string(), User.class);
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
}
