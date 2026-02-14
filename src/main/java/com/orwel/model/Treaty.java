package com.orwel.model;

import java.time.LocalDate;

public class Treaty {
    private Integer treatyId;
    private Integer govId;
    private String officialNumber;
    private String title;
    private String foreignPartner;
    private Integer tagId;
    private LocalDate transmissionDate;
    private String currentStatus;
    private String officialSummary;
    private String documentUrl;
    
    // For easier display
    private Government government;
    private Tag tag;
    
    // Getters and Setters
    public Integer getTreatyId() { return treatyId; }
    public void setTreatyId(Integer treatyId) { this.treatyId = treatyId; }
    
    public Integer getGovId() { return govId; }
    public void setGovId(Integer govId) { this.govId = govId; }
    
    public String getOfficialNumber() { return officialNumber; }
    public void setOfficialNumber(String officialNumber) { this.officialNumber = officialNumber; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getForeignPartner() { return foreignPartner; }
    public void setForeignPartner(String foreignPartner) { this.foreignPartner = foreignPartner; }
    
    public Integer getTagId() { return tagId; }
    public void setTagId(Integer tagId) { this.tagId = tagId; }
    
    public LocalDate getTransmissionDate() { return transmissionDate; }
    public void setTransmissionDate(LocalDate transmissionDate) { this.transmissionDate = transmissionDate; }
    
    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
    
    public String getOfficialSummary() { return officialSummary; }
    public void setOfficialSummary(String officialSummary) { this.officialSummary = officialSummary; }
    
    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }
    
    public Government getGovernment() { return government; }
    public void setGovernment(Government government) { this.government = government; }
    
    public Tag getTag() { return tag; }
    public void setTag(Tag tag) { this.tag = tag; }
}
