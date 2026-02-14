package com.orwel.model;

import java.time.LocalDate;

public class CommitteeMaterial {
    private Integer matId;
    private Integer comId;
    private Integer legId;
    private String materialType;  // 'Report', 'Hearing', 'Markup', 'Impact Assessment', 'Print'
    private String title;
    private String officialRefNumber;
    private String officialSummary;
    private String sectionAnalysis;  // JSON string
    private Double fiscalImpactValue;
    private LocalDate eventDate;
    private String documentUrl;
    
    // For easier display
    private Committee committee;
    private Legislation legislation;
    
    // Getters and Setters
    public Integer getMatId() { return matId; }
    public void setMatId(Integer matId) { this.matId = matId; }
    
    public Integer getComId() { return comId; }
    public void setComId(Integer comId) { this.comId = comId; }
    
    public Integer getLegId() { return legId; }
    public void setLegId(Integer legId) { this.legId = legId; }
    
    public String getMaterialType() { return materialType; }
    public void setMaterialType(String materialType) { this.materialType = materialType; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getOfficialRefNumber() { return officialRefNumber; }
    public void setOfficialRefNumber(String officialRefNumber) { this.officialRefNumber = officialRefNumber; }
    
    public String getOfficialSummary() { return officialSummary; }
    public void setOfficialSummary(String officialSummary) { this.officialSummary = officialSummary; }
    
    public String getSectionAnalysis() { return sectionAnalysis; }
    public void setSectionAnalysis(String sectionAnalysis) { this.sectionAnalysis = sectionAnalysis; }
    
    public Double getFiscalImpactValue() { return fiscalImpactValue; }
    public void setFiscalImpactValue(Double fiscalImpactValue) { this.fiscalImpactValue = fiscalImpactValue; }
    
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    
    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }
    
    public Committee getCommittee() { return committee; }
    public void setCommittee(Committee committee) { this.committee = committee; }
    
    public Legislation getLegislation() { return legislation; }
    public void setLegislation(Legislation legislation) { this.legislation = legislation; }
}
