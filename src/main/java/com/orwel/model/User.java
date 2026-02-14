package com.orwel.model;

import java.util.List;

public class User {
    private Long id;
    private String username;
    private String email;
    private String password; // Should be hashed
    private String firstName;
    private String lastName;
    private String occupation;
    private Boolean hasStocks;
    private List<String> commodityTags; // User's commodity interests: "oil", "gold", "IT sector", etc.
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    
    public Boolean getHasStocks() { return hasStocks; }
    public void setHasStocks(Boolean hasStocks) { this.hasStocks = hasStocks; }
    
    public List<String> getCommodityTags() { return commodityTags; }
    public void setCommodityTags(List<String> commodityTags) { this.commodityTags = commodityTags; }
}
