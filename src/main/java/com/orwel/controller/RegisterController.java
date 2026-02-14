package com.orwel.controller;

import com.orwel.model.AuthResponse;
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
import java.util.Arrays;
import java.util.List;

public class RegisterController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField addressField;
    @FXML private TextField cityField;
    @FXML private TextField postalCodeField;
    @FXML private ComboBox<String> countryComboBox;
    @FXML private Button geocodeButton;
    @FXML private Label locationStatusLabel;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private ComboBox<String> marriageStatusComboBox;
    @FXML private TextField phoneNumberField;
    @FXML private TextField occupationField;
    @FXML private CheckBox hasStocksCheckbox;
    @FXML private CheckBox plansToTravelCheckbox;
    @FXML private CheckBox interestPolitics;
    @FXML private CheckBox interestTax;
    @FXML private CheckBox interestTravel;
    @FXML private CheckBox interestStocks;
    @FXML private CheckBox interestImmigration;
    @FXML private CheckBox interestTrade;
    @FXML private Button registerButton;
    @FXML private Button cancelButton;
    @FXML private Label errorLabel;
    
    private ApiService apiService = ApiService.getInstance();
    private LocationService locationService = LocationService.getInstance();
    private LocationInfo locationInfo;
    
    @FXML
    public void initialize() {
        // Populate country combo box (common countries)
        List<String> countries = Arrays.asList(
            "United States", "United Kingdom", "Canada", "Australia", "Germany",
            "France", "Italy", "Spain", "Japan", "China", "India", "Brazil",
            "Mexico", "South Korea", "Netherlands", "Sweden", "Norway", "Switzerland"
        );
        countryComboBox.setItems(FXCollections.observableArrayList(countries));
        
        // Populate marriage status combo box
        List<String> statuses = Arrays.asList(
            "SINGLE", "MARRIED", "DIVORCED", "WIDOWED", "DOMESTIC_PARTNERSHIP"
        );
        marriageStatusComboBox.setItems(FXCollections.observableArrayList(statuses));
        
        // Set date picker to allow dates from 1900 to today
        dateOfBirthPicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()) || date.isBefore(LocalDate.of(1900, 1, 1)));
            }
        });
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
                    if (info.getCountryCode() != null) {
                        // Try to match country
                        String countryName = getCountryNameFromCode(info.getCountryCode());
                        if (countryName != null && countryComboBox.getItems().contains(countryName)) {
                            countryComboBox.setValue(countryName);
                        }
                    }
                    if (info.getRegion() != null) {
                        // Could add region field if needed
                    }
                    locationStatusLabel.setText("Location found: " + info.getFormattedAddress());
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
    
    private String getCountryNameFromCode(String code) {
        // Simple mapping - could be expanded
        switch (code) {
            case "US": return "United States";
            case "GB": return "United Kingdom";
            case "CA": return "Canada";
            case "AU": return "Australia";
            case "DE": return "Germany";
            case "FR": return "France";
            case "IT": return "Italy";
            case "ES": return "Spain";
            case "JP": return "Japan";
            case "CN": return "China";
            case "IN": return "India";
            case "BR": return "Brazil";
            case "MX": return "Mexico";
            case "KR": return "South Korea";
            case "NL": return "Netherlands";
            case "SE": return "Sweden";
            case "NO": return "Norway";
            case "CH": return "Switzerland";
            default: return null;
        }
    }
    
    @FXML
    private void handleRegister() {
        // Validate form
        if (!validateForm()) {
            return;
        }
        
        registerButton.setDisable(true);
        errorLabel.setVisible(false);
        
        // Create user object
        User user = new User();
        user.setFirstName(firstNameField.getText().trim());
        user.setLastName(lastNameField.getText().trim());
        user.setUsername(usernameField.getText().trim());
        user.setEmail(emailField.getText().trim());
        user.setPassword(passwordField.getText()); // Backend should hash this
        user.setOccupation(occupationField != null ? occupationField.getText().trim() : "");
        user.setHasStocks(hasStocksCheckbox != null && hasStocksCheckbox.isSelected());
        
        // Register user
        new Thread(() -> {
            try {
                AuthResponse response = apiService.register(user);
                Platform.runLater(() -> {
                    if (response.isSuccess() && response.getToken() != null) {
                        // Navigate to dashboard
                        navigateToDashboard();
                    } else {
                        showError(response.getMessage() != null ? response.getMessage() : "Registration failed");
                        registerButton.setDisable(false);
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showError("Connection error: " + e.getMessage());
                    registerButton.setDisable(false);
                });
            }
        }).start();
    }
    
    private boolean validateForm() {
        if (firstNameField.getText().trim().isEmpty() ||
            lastNameField.getText().trim().isEmpty() ||
            usernameField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() ||
            passwordField.getText().isEmpty() ||
            addressField.getText().trim().isEmpty() ||
            cityField.getText().trim().isEmpty() ||
            countryComboBox.getValue() == null) {
            showError("Please fill in all required fields");
            return false;
        }
        
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Passwords do not match");
            return false;
        }
        
        if (passwordField.getText().length() < 6) {
            showError("Password must be at least 6 characters long");
            return false;
        }
        
        if (!emailField.getText().trim().contains("@")) {
            showError("Please enter a valid email address");
            return false;
        }
        
        return true;
    }
    
    @FXML
    private void handleCancel() {
        NavigationHelper.navigate(cancelButton, "/fxml/Login.fxml");
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private void navigateToDashboard() {
        NavigationHelper.navigate(registerButton, "/fxml/Dashboard.fxml");
    }
}
