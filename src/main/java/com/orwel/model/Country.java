package com.orwel.model;

import java.util.List;

public class Country {
    private String code; // ISO country code
    private String name;
    private String flag; // URL or emoji
    private List<Policy> policies;
    private List<Stance> stances;
    private List<String> abidesBy; // What they follow
    private List<String> doesNotFollow; // What they don't follow
    private String summary;
    private List<Warning> warnings;
    
    // Getters and Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getFlag() { return flag; }
    public void setFlag(String flag) { this.flag = flag; }
    
    public List<Policy> getPolicies() { return policies; }
    public void setPolicies(List<Policy> policies) { this.policies = policies; }
    
    public List<Stance> getStances() { return stances; }
    public void setStances(List<Stance> stances) { this.stances = stances; }
    
    public List<String> getAbidesBy() { return abidesBy; }
    public void setAbidesBy(List<String> abidesBy) { this.abidesBy = abidesBy; }
    
    public List<String> getDoesNotFollow() { return doesNotFollow; }
    public void setDoesNotFollow(List<String> doesNotFollow) { this.doesNotFollow = doesNotFollow; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public List<Warning> getWarnings() { return warnings; }
    public void setWarnings(List<Warning> warnings) { this.warnings = warnings; }
}
