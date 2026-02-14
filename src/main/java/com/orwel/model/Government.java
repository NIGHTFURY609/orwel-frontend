package com.orwel.model;

public class Government {
    private Integer govId;
    private String name;
    private String jurisdiction;  // 'US' or 'EU'
    private String branch;  // 'Legislative', 'Executive', 'Committee'
    
    // Getters and Setters
    public Integer getGovId() { return govId; }
    public void setGovId(Integer govId) { this.govId = govId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getJurisdiction() { return jurisdiction; }
    public void setJurisdiction(String jurisdiction) { this.jurisdiction = jurisdiction; }
    
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
}
