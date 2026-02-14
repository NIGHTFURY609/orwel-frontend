package com.orwel.model;

import java.time.LocalDateTime;
import java.util.List;

public class NewsArticle {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String source;
    private String author;
    private LocalDateTime publishedAt;
    private String url;
    private String imageUrl;
    private String[] tags;
    private String countryCode;
    private String region;
    private List<String> relatedPoliticians;
    private String category; // personalized, general, politics, etc.
    private Double relevanceScore; // For personalized news
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String[] getTags() { return tags; }
    public void setTags(String[] tags) { this.tags = tags; }
    
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public List<String> getRelatedPoliticians() { return relatedPoliticians; }
    public void setRelatedPoliticians(List<String> relatedPoliticians) { this.relatedPoliticians = relatedPoliticians; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Double getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(Double relevanceScore) { this.relevanceScore = relevanceScore; }
}
