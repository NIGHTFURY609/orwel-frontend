package com.orwel.model;

import java.time.LocalDate;

public class LegislativeAction {
    private Integer actionId;
    private Integer legId;
    private Integer govId;
    private Integer treatyId;
    private LocalDate actionDate;
    private String actionType;  // 'Amendment', 'Vote', 'Committee Report'
    private String description;
    private String rollCallId;
    private Integer actingGovMemId;
    
    // For easier display
    private Legislation legislation;
    private Government government;
    private Treaty treaty;
    private GovernmentMember actingMember;
    
    // Getters and Setters
    public Integer getActionId() { return actionId; }
    public void setActionId(Integer actionId) { this.actionId = actionId; }
    
    public Integer getLegId() { return legId; }
    public void setLegId(Integer legId) { this.legId = legId; }
    
    public Integer getGovId() { return govId; }
    public void setGovId(Integer govId) { this.govId = govId; }
    
    public Integer getTreatyId() { return treatyId; }
    public void setTreatyId(Integer treatyId) { this.treatyId = treatyId; }
    
    public LocalDate getActionDate() { return actionDate; }
    public void setActionDate(LocalDate actionDate) { this.actionDate = actionDate; }
    
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getRollCallId() { return rollCallId; }
    public void setRollCallId(String rollCallId) { this.rollCallId = rollCallId; }
    
    public Integer getActingGovMemId() { return actingGovMemId; }
    public void setActingGovMemId(Integer actingGovMemId) { this.actingGovMemId = actingGovMemId; }
    
    public Legislation getLegislation() { return legislation; }
    public void setLegislation(Legislation legislation) { this.legislation = legislation; }
    
    public Government getGovernment() { return government; }
    public void setGovernment(Government government) { this.government = government; }
    
    public Treaty getTreaty() { return treaty; }
    public void setTreaty(Treaty treaty) { this.treaty = treaty; }
    
    public GovernmentMember getActingMember() { return actingMember; }
    public void setActingMember(GovernmentMember actingMember) { this.actingMember = actingMember; }
}
