package com.orwel.model;

import java.time.LocalDate;

public class Legislation {
    private Integer legId;
    private String billType;  // US: 's', 'hr' | EU: 'COD', 'APP', 'NLE'
    private String billNumber;
    private String refCode;  // 'H.R. 101' or '2024/0123(COD)'
    private Integer tagId;
    private String title;
    private String summary;
    private String policyArea;
    private Integer sponsorGovMemId;
    private Integer cosponsorGovMemId;
    private Integer initiatorCommissionerId;
    private LocalDate dateIntroduced;
    private String currentStatus;
    
    // For easier display
    private Tag tag;
    private GovernmentMember sponsor;
    private GovernmentMember cosponsor;
    
    // Getters and Setters
    public Integer getLegId() { return legId; }
    public void setLegId(Integer legId) { this.legId = legId; }
    
    public String getBillType() { return billType; }
    public void setBillType(String billType) { this.billType = billType; }
    
    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
    
    public String getRefCode() { return refCode; }
    public void setRefCode(String refCode) { this.refCode = refCode; }
    
    public Integer getTagId() { return tagId; }
    public void setTagId(Integer tagId) { this.tagId = tagId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public String getPolicyArea() { return policyArea; }
    public void setPolicyArea(String policyArea) { this.policyArea = policyArea; }
    
    public Integer getSponsorGovMemId() { return sponsorGovMemId; }
    public void setSponsorGovMemId(Integer sponsorGovMemId) { this.sponsorGovMemId = sponsorGovMemId; }
    
    public Integer getCosponsorGovMemId() { return cosponsorGovMemId; }
    public void setCosponsorGovMemId(Integer cosponsorGovMemId) { this.cosponsorGovMemId = cosponsorGovMemId; }
    
    public Integer getInitiatorCommissionerId() { return initiatorCommissionerId; }
    public void setInitiatorCommissionerId(Integer initiatorCommissionerId) { this.initiatorCommissionerId = initiatorCommissionerId; }
    
    public LocalDate getDateIntroduced() { return dateIntroduced; }
    public void setDateIntroduced(LocalDate dateIntroduced) { this.dateIntroduced = dateIntroduced; }
    
    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
    
    public Tag getTag() { return tag; }
    public void setTag(Tag tag) { this.tag = tag; }
    
    public GovernmentMember getSponsor() { return sponsor; }
    public void setSponsor(GovernmentMember sponsor) { this.sponsor = sponsor; }
    
    public GovernmentMember getCosponsor() { return cosponsor; }
    public void setCosponsor(GovernmentMember cosponsor) { this.cosponsor = cosponsor; }
}
