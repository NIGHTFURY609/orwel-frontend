package com.orwel.model;

public class AuthResponse {
    private String token;
    private String access_token;  // Supabase format
    private User user;
    private boolean success;
    private String message;
    
    public AuthResponse() {}
    
    public String getToken() { return token != null ? token : access_token; }
    public void setToken(String token) { this.token = token; }
    
    public String getAccessToken() { return access_token != null ? access_token : token; }
    public void setAccessToken(String accessToken) { this.access_token = accessToken; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
