package com.orwel.model;

import java.time.LocalDate;

public class ResearchReport {
    private Integer reportId;
    private Integer govId;
    private String reportNumber;  // US: 'R47123' | EU: 'PE 756.123'
    private String title;
    private Integer tagId;
    private Integer legId;
    private String summaryText;
    private LocalDate datePublished;
    private String documentUrl;
    
    // For easier display
    private Government government;
    private Tag tag;
    private Legislation legislation;
    
    // Getters and Setters
    public Integer getReportId() { return reportId; }
    public void setReportId(Integer reportId) { this.reportId = reportId; }
    
    public Integer getGovId() { return govId; }
    public void setGovId(Integer govId) { this.govId = govId; }
    
    public String getReportNumber() { return reportNumber; }
    public void setReportNumber(String reportNumber) { this.reportNumber = reportNumber; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public Integer getTagId() { return tagId; }
    public void setTagId(Integer tagId) { this.tagId = tagId; }
    
    public Integer getLegId() { return legId; }
    public void setLegId(Integer legId) { this.legId = legId; }
    
    public String getSummaryText() { return summaryText; }
    public void setSummaryText(String summaryText) { this.summaryText = summaryText; }
    
    public LocalDate getDatePublished() { return datePublished; }
    public void setDatePublished(LocalDate datePublished) { this.datePublished = datePublished; }
    
    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }
    
    public Government getGovernment() { return government; }
    public void setGovernment(Government government) { this.government = government; }
    
    public Tag getTag() { return tag; }
    public void setTag(Tag tag) { this.tag = tag; }
    
    public Legislation getLegislation() { return legislation; }
    public void setLegislation(Legislation legislation) { this.legislation = legislation; }
}
