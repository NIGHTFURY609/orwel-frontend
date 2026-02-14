package com.orwel.model;

import java.time.LocalDate;

public class Nomination {
    private Integer nomId;
    private Integer memId;
    private Integer confirmingGovId;
    private String positionTitle;
    private String targetOrganization;
    private Integer tagId;
    private LocalDate dateReceived;
    private String currentStatus;
    private String officialSummary;
    private String documentUrl;
    
    // For easier display
    private Member member;
    private Government confirmingGovernment;
    private Tag tag;
    
    // Getters and Setters
    public Integer getNomId() { return nomId; }
    public void setNomId(Integer nomId) { this.nomId = nomId; }
    
    public Integer getMemId() { return memId; }
    public void setMemId(Integer memId) { this.memId = memId; }
    
    public Integer getConfirmingGovId() { return confirmingGovId; }
    public void setConfirmingGovId(Integer confirmingGovId) { this.confirmingGovId = confirmingGovId; }
    
    public String getPositionTitle() { return positionTitle; }
    public void setPositionTitle(String positionTitle) { this.positionTitle = positionTitle; }
    
    public String getTargetOrganization() { return targetOrganization; }
    public void setTargetOrganization(String targetOrganization) { this.targetOrganization = targetOrganization; }
    
    public Integer getTagId() { return tagId; }
    public void setTagId(Integer tagId) { this.tagId = tagId; }
    
    public LocalDate getDateReceived() { return dateReceived; }
    public void setDateReceived(LocalDate dateReceived) { this.dateReceived = dateReceived; }
    
    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
    
    public String getOfficialSummary() { return officialSummary; }
    public void setOfficialSummary(String officialSummary) { this.officialSummary = officialSummary; }
    
    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }
    
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
    
    public Government getConfirmingGovernment() { return confirmingGovernment; }
    public void setConfirmingGovernment(Government confirmingGovernment) { this.confirmingGovernment = confirmingGovernment; }
    
    public Tag getTag() { return tag; }
    public void setTag(Tag tag) { this.tag = tag; }
}
