package com.orwel.model;

import java.time.LocalDate;

public class User {
    private Long id;
    private String username;
    private String email;
    private String password; // Should be hashed
    private String firstName;
    private String lastName;
    private String address;
    private String country;
    private String city;
    private String postalCode;
    private String phoneNumber;
    private MarriageStatus marriageStatus;
    private LocalDate dateOfBirth;
    private String occupation;
    private Boolean hasStocks;
    private Boolean plansToTravel;
    private String[] interests; // e.g., ["politics", "tax", "travel"]
    private LocationInfo locationInfo;
    
    public enum MarriageStatus {
        SINGLE, MARRIED, DIVORCED, WIDOWED, DOMESTIC_PARTNERSHIP
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public MarriageStatus getMarriageStatus() { return marriageStatus; }
    public void setMarriageStatus(MarriageStatus marriageStatus) { this.marriageStatus = marriageStatus; }
    
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    
    public Boolean getHasStocks() { return hasStocks; }
    public void setHasStocks(Boolean hasStocks) { this.hasStocks = hasStocks; }
    
    public Boolean getPlansToTravel() { return plansToTravel; }
    public void setPlansToTravel(Boolean plansToTravel) { this.plansToTravel = plansToTravel; }
    
    public String[] getInterests() { return interests; }
    public void setInterests(String[] interests) { this.interests = interests; }
    
    public LocationInfo getLocationInfo() { return locationInfo; }
    public void setLocationInfo(LocationInfo locationInfo) { this.locationInfo = locationInfo; }
}
