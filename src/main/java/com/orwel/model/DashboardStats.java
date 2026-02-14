package com.orwel.model;

public class DashboardStats {
    private Integer totalLegislation;
    private Integer totalHearings;
    private Integer totalNominations;
    private Integer totalTreaties;
    private Integer totalCommittees;
    private Integer recentLegislationCount;  // Last 30 days
    private Integer recentHearingsCount;     // Last 30 days
    
    // Getters and Setters
    public Integer getTotalLegislation() { return totalLegislation; }
    public void setTotalLegislation(Integer totalLegislation) { this.totalLegislation = totalLegislation; }
    
    public Integer getTotalHearings() { return totalHearings; }
    public void setTotalHearings(Integer totalHearings) { this.totalHearings = totalHearings; }
    
    public Integer getTotalNominations() { return totalNominations; }
    public void setTotalNominations(Integer totalNominations) { this.totalNominations = totalNominations; }
    
    public Integer getTotalTreaties() { return totalTreaties; }
    public void setTotalTreaties(Integer totalTreaties) { this.totalTreaties = totalTreaties; }
    
    public Integer getTotalCommittees() { return totalCommittees; }
    public void setTotalCommittees(Integer totalCommittees) { this.totalCommittees = totalCommittees; }
    
    public Integer getRecentLegislationCount() { return recentLegislationCount; }
    public void setRecentLegislationCount(Integer recentLegislationCount) { this.recentLegislationCount = recentLegislationCount; }
    
    public Integer getRecentHearingsCount() { return recentHearingsCount; }
    public void setRecentHearingsCount(Integer recentHearingsCount) { this.recentHearingsCount = recentHearingsCount; }
}
