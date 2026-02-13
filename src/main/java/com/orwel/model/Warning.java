package com.orwel.model;

public class Warning {
    private Long id;
    private String title;
    private String message;
    private String severity; // low, medium, high, critical
    private String category; // stocks, travel, tax, immigration, etc.
    private String countryCode;
    private String[] affectedUserTypes; // e.g., ["stock_holders", "travelers", "married_couples"]
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    
    public String[] getAffectedUserTypes() { return affectedUserTypes; }
    public void setAffectedUserTypes(String[] affectedUserTypes) { this.affectedUserTypes = affectedUserTypes; }
}
