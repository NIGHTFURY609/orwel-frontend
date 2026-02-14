package com.orwel.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.orwel.model.LocationInfo;
import com.orwel.model.User;
import com.orwel.service.ApiService;
import com.orwel.service.LocationService;
import com.orwel.util.NavigationHelper;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ProfileController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField addressField;
    @FXML private TextField cityField;
    @FXML private TextField postalCodeField;
    @FXML private ComboBox<String> countryComboBox;
    @FXML private Button geocodeButton;
    @FXML private Label locationStatusLabel;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private ComboBox<String> marriageStatusComboBox;
    @FXML private TextField occupationField;
    @FXML private CheckBox hasStocksCheckbox;
    @FXML private CheckBox plansToTravelCheckbox;
    @FXML private CheckBox interestPolitics;
    @FXML private CheckBox interestTax;
    @FXML private CheckBox interestTravel;
    @FXML private CheckBox interestStocks;
    @FXML private CheckBox interestImmigration;
    @FXML private CheckBox interestTrade;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    
    // Investment profile fields
    @FXML private ComboBox<String> investmentExperienceComboBox;
    @FXML private ComboBox<String> riskToleranceComboBox;
    @FXML private ComboBox<String> tradingStyleComboBox;
    @FXML private ComboBox<String> portfolioSizeComboBox;
    @FXML private ComboBox<String> tradingFrequencyComboBox;
    @FXML private ComboBox<String> timeHorizonComboBox;
    @FXML private ComboBox<String> liquidAssetsComboBox;
    @FXML private ComboBox<String> taxStrategyComboBox;
    @FXML private ComboBox<String> sourceComboBox;
    @FXML private CheckBox goalCapitalGrowth;
    @FXML private CheckBox goalIncomeGeneration;
    @FXML private CheckBox goalWealth;
    @FXML private CheckBox goalRetirement;
    @FXML private CheckBox goalShortTerm;
    @FXML private CheckBox secEquities;
    @FXML private CheckBox secOptions;
    @FXML private CheckBox secBonds;
    @FXML private CheckBox secCommodities;
    @FXML private CheckBox secCrypto;
    @FXML private CheckBox secMutualFunds;
    @FXML private CheckBox analysisFundamental;
    @FXML private CheckBox analysisTechnical;
    @FXML private CheckBox analysisQuantitative;
    @FXML private CheckBox analysisNews;
    @FXML private CheckBox dividendFocus;
    @FXML private CheckBox dividendReinvest;
    @FXML private CheckBox dividendIgnore;
    @FXML private CheckBox riskStopLoss;
    @FXML private CheckBox riskHedging;
    @FXML private CheckBox riskDiversification;
    @FXML private CheckBox riskPortfolioRebalance;
    @FXML private CheckBox intlDeveloped;
    @FXML private CheckBox intlEmerging;
    @FXML private CheckBox intlFrontier;
    @FXML private TextArea investmentNotesArea;
    
    private ApiService apiService = ApiService.getInstance();
    private LocationService locationService = LocationService.getInstance();
    private LocationInfo locationInfo;
    
    @FXML
    public void initialize() {
        try {
            // Populate country combo boxes
            List<String> countries = Arrays.asList(
                "United States", "United Kingdom", "Canada", "Australia", "Germany",
                "France", "Italy", "Spain", "Japan", "China", "India", "Brazil",
                "Mexico", "South Korea", "Netherlands", "Sweden", "Norway", "Switzerland"
            );
            countryComboBox.setItems(FXCollections.observableArrayList(countries));
            
            List<String> statuses = Arrays.asList(
                "SINGLE", "MARRIED", "DIVORCED", "WIDOWED", "DOMESTIC_PARTNERSHIP"
            );
            marriageStatusComboBox.setItems(FXCollections.observableArrayList(statuses));
            
            // Populate investment experience
            investmentExperienceComboBox.setItems(FXCollections.observableArrayList(
                "Beginner", "Intermediate", "Advanced", "Professional"
            ));
            
            // Populate risk tolerance
            riskToleranceComboBox.setItems(FXCollections.observableArrayList(
                "Conservative", "Moderate", "Aggressive", "Very Aggressive"
            ));
            
            // Populate trading style
            tradingStyleComboBox.setItems(FXCollections.observableArrayList(
                "Long-term Investing", "Swing Trading", "Day Trading", "Dividend Focus", "Mixed Strategy"
            ));
            
            // Populate portfolio size
            portfolioSizeComboBox.setItems(FXCollections.observableArrayList(
                "Less than $10,000", "$10,000 - $50,000", "$50,000 - $100,000", 
                "$100,000 - $500,000", "$500,000 - $1M", "Above $1M"
            ));
            
            // Populate trading frequency
            tradingFrequencyComboBox.setItems(FXCollections.observableArrayList(
                "Never (Buy & Hold)", "Few times per year", "Monthly", "Weekly", "Daily", "Multiple times per day"
            ));
            
            // Populate time horizon
            timeHorizonComboBox.setItems(FXCollections.observableArrayList(
                "Less than 1 month", "1-3 months", "3-6 months", "6-12 months", 
                "1-3 years", "3-5 years", "5+ years"
            ));
            
            // Populate liquid assets
            liquidAssetsComboBox.setItems(FXCollections.observableArrayList(
                "Less than $5,000", "$5,000 - $25,000", "$25,000 - $100,000",
                "$100,000 - $500,000", "$500,000 - $2M", "Above $2M"
            ));
            
            // Populate tax strategy
            taxStrategyComboBox.setItems(FXCollections.observableArrayList(
                "Actively pursue tax-loss harvesting", "Occasionally consider tax implications",
                "Minimal focus on tax optimization", "Use tax-advantaged accounts primarily"
            ));
            
            // Populate source discovery
            sourceComboBox.setItems(FXCollections.observableArrayList(
                "Search Engine", "Social Media", "Friend or Family Recommendation",
                "Investment Community/Forum", "Business Publication", "App Store", "Other"
            ));
            
            // Load user data
            loadUserData();
        } catch (Exception e) {
            System.err.println("[ProfileController] Error in initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadUserData() {
        new Thread(() -> {
            try {
                User user = apiService.getCurrentUser();
                if (user != null) {
                    Platform.runLater(() -> {
                        populateForm(user);
                    });
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showError("Failed to load user data: " + e.getMessage());
                });
            }
        }).start();
    }
    
    private void populateForm(User user) {
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        phoneNumberField.setText(user.getPhoneNumber());
        addressField.setText(user.getAddress());
        cityField.setText(user.getCity());
        postalCodeField.setText(user.getPostalCode());
        if (user.getCountry() != null) {
            countryComboBox.setValue(user.getCountry());
        }
        dateOfBirthPicker.setValue(user.getDateOfBirth());
        if (user.getMarriageStatus() != null) {
            marriageStatusComboBox.setValue(user.getMarriageStatus().name());
        }
        occupationField.setText(user.getOccupation());
        hasStocksCheckbox.setSelected(user.getHasStocks() != null && user.getHasStocks());
        plansToTravelCheckbox.setSelected(user.getPlansToTravel() != null && user.getPlansToTravel());
        
        if (user.getInterests() != null) {
            List<String> interests = Arrays.asList(user.getInterests());
            interestPolitics.setSelected(interests.contains("politics"));
            interestTax.setSelected(interests.contains("tax"));
            interestTravel.setSelected(interests.contains("travel"));
            interestStocks.setSelected(interests.contains("stocks"));
            interestImmigration.setSelected(interests.contains("immigration"));
            interestTrade.setSelected(interests.contains("trade"));
        }
        
        if (user.getLocationInfo() != null) {
            locationInfo = user.getLocationInfo();
        }
    }
    
    @FXML
    private void handleGeocode() {
        String address = addressField.getText().trim();
        if (address.isEmpty()) {
            showError("Please enter an address first");
            return;
        }
        
        geocodeButton.setDisable(true);
        locationStatusLabel.setText("Geocoding address...");
        locationStatusLabel.setVisible(true);
        
        new Thread(() -> {
            try {
                LocationInfo info = locationService.geocodeAddress(address);
                Platform.runLater(() -> {
                    locationInfo = info;
                    if (info.getCity() != null && cityField.getText().isEmpty()) {
                        cityField.setText(info.getCity());
                    }
                    locationStatusLabel.setText("Location updated: " + info.getFormattedAddress());
                    locationStatusLabel.setStyle("-fx-text-fill: #27ae60;");
                    geocodeButton.setDisable(false);
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    locationStatusLabel.setText("Geocoding failed: " + e.getMessage());
                    locationStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
                    geocodeButton.setDisable(false);
                });
            }
        }).start();
    }
    
    @FXML
    private void handleSave() {
        saveButton.setDisable(true);
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
        
        // Create updated user object
        User user = new User();
        user.setFirstName(firstNameField.getText().trim());
        user.setLastName(lastNameField.getText().trim());
        user.setEmail(emailField.getText().trim());
        user.setPhoneNumber(phoneNumberField.getText().trim());
        user.setAddress(addressField.getText().trim());
        user.setCity(cityField.getText().trim());
        user.setPostalCode(postalCodeField.getText().trim());
        user.setCountry(countryComboBox.getValue());
        user.setDateOfBirth(dateOfBirthPicker.getValue());
        if (marriageStatusComboBox.getValue() != null) {
            user.setMarriageStatus(User.MarriageStatus.valueOf(marriageStatusComboBox.getValue()));
        }
        user.setOccupation(occupationField.getText().trim());
        user.setHasStocks(hasStocksCheckbox.isSelected());
        user.setPlansToTravel(plansToTravelCheckbox.isSelected());
        
        List<String> interests = new ArrayList<>();
        if (interestPolitics.isSelected()) interests.add("politics");
        if (interestTax.isSelected()) interests.add("tax");
        if (interestTravel.isSelected()) interests.add("travel");
        if (interestStocks.isSelected()) interests.add("stocks");
        if (interestImmigration.isSelected()) interests.add("immigration");
        if (interestTrade.isSelected()) interests.add("trade");
        user.setInterests(interests.toArray(new String[0]));
        
        if (locationInfo != null) {
            user.setLocationInfo(locationInfo);
        }
        
        new Thread(() -> {
            try {
                User updatedUser = apiService.updateUser(user);
                Platform.runLater(() -> {
                    if (updatedUser != null) {
                        showSuccess("Profile updated successfully!");
                        saveButton.setDisable(false);
                    } else {
                        showError("Failed to update profile");
                        saveButton.setDisable(false);
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showError("Connection error: " + e.getMessage());
                    saveButton.setDisable(false);
                });
            }
        }).start();
    }
    
    @FXML
    private void handleCancel() {
        loadUserData(); // Reload original data
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
    }
    
    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
    }
    
    // Navigation methods
    @FXML
    private void navigateToDashboard() {
        NavigationHelper.navigate(saveButton, "/fxml/Dashboard.fxml");
    }
    
    @FXML
    private void navigateToCountries() {
        NavigationHelper.navigate(saveButton, "/fxml/Countries.fxml");
    }
    
    @FXML
    private void navigateToNews() {
        NavigationHelper.navigate(saveButton, "/fxml/News.fxml");
    }
    
    @FXML
    private void navigateToProfile() {
        // Already on profile
    }
    
    @FXML
    private void navigateToAbout() {
        NavigationHelper.navigate(saveButton, "/fxml/About.fxml");
    }
    
    @FXML
    private void handleLogout() {
        apiService.logout();
        NavigationHelper.navigate(saveButton, "/fxml/Login.fxml");
    }
}
