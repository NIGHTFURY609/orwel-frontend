package com.orwel.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.orwel.model.User;
import com.orwel.service.ApiService;
import com.orwel.util.NavigationHelper;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ProfileController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField occupationField;
    @FXML private CheckBox hasStocksCheckbox;
    @FXML private TextArea commodityTagsArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private Label tagsHelpLabel;
    
    private ApiService apiService = ApiService.getInstance();
    
    @FXML
    public void initialize() {
        try {
            // Set help text for tags
            if (tagsHelpLabel != null) {
                tagsHelpLabel.setText("Enter comma-separated commodity tags (e.g., oil, gold, IT sector, iron, finance)");
            }
            
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
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Failed to load user data: " + e.getMessage());
                });
            }
        }).start();
    }
    
    private void populateForm(User user) {
        if (firstNameField != null) firstNameField.setText(user.getFirstName());
        if (lastNameField != null) lastNameField.setText(user.getLastName());
        if (emailField != null) emailField.setText(user.getEmail());
        if (occupationField != null) occupationField.setText(user.getOccupation());
        if (hasStocksCheckbox != null) hasStocksCheckbox.setSelected(user.getHasStocks() != null && user.getHasStocks());
        
        // Display commodity tags
        if (commodityTagsArea != null && user.getCommodityTags() != null) {
            String tagsText = String.join(", ", user.getCommodityTags());
            commodityTagsArea.setText(tagsText);
        }
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
        user.setOccupation(occupationField.getText().trim());
        user.setHasStocks(hasStocksCheckbox.isSelected());
        
        // Parse commodity tags
        String tagsText = commodityTagsArea.getText().trim();
        if (!tagsText.isEmpty()) {
            List<String> tags = Arrays.stream(tagsText.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            user.setCommodityTags(tags);
            
            // Also save tags via dedicated endpoint
            new Thread(() -> {
                try {
                    apiService.saveUserTags(tags);
                } catch (IOException e) {
                    System.err.println("Failed to save tags: " + e.getMessage());
                }
            }).start();
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
        NavigationHelper.navigate(saveButton, "/fxml/Updates.fxml");
    }
    
    @FXML
    private void navigateToUpdates() {
        NavigationHelper.navigate(saveButton, "/fxml/Updates.fxml");
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
