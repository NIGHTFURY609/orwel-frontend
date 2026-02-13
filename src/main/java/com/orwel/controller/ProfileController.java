package com.orwel.controller;

import com.orwel.model.LocationInfo;
import com.orwel.model.User;
import com.orwel.service.ApiService;
import com.orwel.service.LocationService;
import com.orwel.util.NavigationHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    
    private ApiService apiService = ApiService.getInstance();
    private LocationService locationService = LocationService.getInstance();
    private LocationInfo locationInfo;
    
    @FXML
    public void initialize() {
        // Populate combo boxes
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
        
        // Load user data
        loadUserData();
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
    @FXML private void navigateToDashboard() {
        navigate("/fxml/Dashboard.fxml");
    }
    
    @FXML private void navigateToCountries() {
        navigate("/fxml/Countries.fxml");
    }
    
    @FXML private void navigateToNews() {
        navigate("/fxml/News.fxml");
    }
    
    @FXML private void navigateToProfile() {
        // Already on profile
    }
    
    @FXML private void navigateToAbout() {
        navigate("/fxml/About.fxml");
    }
    
    @FXML private void handleLogout() {
        apiService.logout();
        navigate("/fxml/Login.fxml");
    }
    
    private void navigate(String fxmlPath) {
        NavigationHelper.navigate(saveButton, fxmlPath);
    }
}
