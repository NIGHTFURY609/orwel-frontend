package com.orwel.model;

public class Tag {
    private Integer tagId;
    private Integer id;  // Alias for Supabase
    private String tagName;
    private String name;  // Alias for Supabase
    private String naicsCode;  // US Sector Code (e.g., '21' for Mining/Oil)
    private String naceCode;   // EU Sector Code (e.g., 'B' for Mining)
    private Double sectorGdpUsd;
    private Double sectorGdpEur;
    
    // Getters and Setters
    public Integer getTagId() { return tagId != null ? tagId : id; }
    public void setTagId(Integer tagId) { this.tagId = tagId; }
    
    public Integer getId() { return id != null ? id : tagId; }
    public void setId(Integer id) { this.id = id; }
    
    public String getTagName() { return tagName != null ? tagName : name; }
    public void setTagName(String tagName) { this.tagName = tagName; }
    
    public String getName() { return name != null ? name : tagName; }
    public void setName(String name) { this.name = name; }
    
    public String getNaicsCode() { return naicsCode; }
    public void setNaicsCode(String naicsCode) { this.naicsCode = naicsCode; }
    
    public String getNaceCode() { return naceCode; }
    public void setNaceCode(String naceCode) { this.naceCode = naceCode; }
    
    public Double getSectorGdpUsd() { return sectorGdpUsd; }
    public void setSectorGdpUsd(Double sectorGdpUsd) { this.sectorGdpUsd = sectorGdpUsd; }
    
    public Double getSectorGdpEur() { return sectorGdpEur; }
    public void setSectorGdpEur(Double sectorGdpEur) { this.sectorGdpEur = sectorGdpEur; }
}
