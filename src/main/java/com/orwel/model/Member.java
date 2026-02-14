package com.orwel.model;

public class Member {
    private Integer memId;
    private String firstName;
    private String lastName;
    private String territoryPrimary;  // 'USA' for US reps, 'France' for EU reps
    private String territorySecondary;
    private String politicalParty;
    private Integer popularity;
    
    // Getters and Setters
    public Integer getMemId() { return memId; }
    public void setMemId(Integer memId) { this.memId = memId; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getTerritoryPrimary() { return territoryPrimary; }
    public void setTerritoryPrimary(String territoryPrimary) { this.territoryPrimary = territoryPrimary; }
    
    public String getTerritorySecondary() { return territorySecondary; }
    public void setTerritorySecondary(String territorySecondary) { this.territorySecondary = territorySecondary; }
    
    public String getPoliticalParty() { return politicalParty; }
    public void setPoliticalParty(String politicalParty) { this.politicalParty = politicalParty; }
    
    public Integer getPopularity() { return popularity; }
    public void setPopularity(Integer popularity) { this.popularity = popularity; }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
