package com.orwel.model;

public class Stance {
    private Long id;
    private String topic; // e.g., "climate change", "trade", "immigration"
    private String position; // e.g., "supports", "opposes", "neutral"
    private String description;
    private String countryCode;
    private String source;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
