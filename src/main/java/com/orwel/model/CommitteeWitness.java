package com.orwel.model;

public class CommitteeWitness {
    private Integer witId;
    private Integer matId;
    private String name;
    private String organization;
    private String positionTitle;
    private String witnessType;  // 'Industry', 'Academic', 'Regulator'
    private Boolean isSp500Related;
    private String testimonySummary;
    
    // For easier display
    private CommitteeMaterial committeeMaterial;
    
    // Getters and Setters
    public Integer getWitId() { return witId; }
    public void setWitId(Integer witId) { this.witId = witId; }
    
    public Integer getMatId() { return matId; }
    public void setMatId(Integer matId) { this.matId = matId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
    
    public String getPositionTitle() { return positionTitle; }
    public void setPositionTitle(String positionTitle) { this.positionTitle = positionTitle; }
    
    public String getWitnessType() { return witnessType; }
    public void setWitnessType(String witnessType) { this.witnessType = witnessType; }
    
    public Boolean getIsSp500Related() { return isSp500Related; }
    public void setIsSp500Related(Boolean isSp500Related) { this.isSp500Related = isSp500Related; }
    
    public String getTestimonySummary() { return testimonySummary; }
    public void setTestimonySummary(String testimonySummary) { this.testimonySummary = testimonySummary; }
    
    public CommitteeMaterial getCommitteeMaterial() { return committeeMaterial; }
    public void setCommitteeMaterial(CommitteeMaterial committeeMaterial) { this.committeeMaterial = committeeMaterial; }
}
