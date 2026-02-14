package com.orwel.model;

public class Committee {
    private Integer comId;
    private Integer govId;
    private String name;
    private String officialCode;  // US: 'SSBK' (Banking) | EU: 'ECON'
    private String apiUrl;
    private Integer tagId;
    
    // For easier display
    private Government government;
    private Tag tag;
    
    // Getters and Setters
    public Integer getComId() { return comId; }
    public void setComId(Integer comId) { this.comId = comId; }
    
    public Integer getGovId() { return govId; }
    public void setGovId(Integer govId) { this.govId = govId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getOfficialCode() { return officialCode; }
    public void setOfficialCode(String officialCode) { this.officialCode = officialCode; }
    
    public String getApiUrl() { return apiUrl; }
    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
    
    public Integer getTagId() { return tagId; }
    public void setTagId(Integer tagId) { this.tagId = tagId; }
    
    public Government getGovernment() { return government; }
    public void setGovernment(Government government) { this.government = government; }
    
    public Tag getTag() { return tag; }
    public void setTag(Tag tag) { this.tag = tag; }
}
