package com.orwel.model;

public class GovernmentMember {
    private Integer gm;
    private Integer govId;
    private Integer memId;
    
    // For easier display, we can include the full objects
    private Government government;
    private Member member;
    
    // Getters and Setters
    public Integer getGm() { return gm; }
    public void setGm(Integer gm) { this.gm = gm; }
    
    public Integer getGovId() { return govId; }
    public void setGovId(Integer govId) { this.govId = govId; }
    
    public Integer getMemId() { return memId; }
    public void setMemId(Integer memId) { this.memId = memId; }
    
    public Government getGovernment() { return government; }
    public void setGovernment(Government government) { this.government = government; }
    
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
}
