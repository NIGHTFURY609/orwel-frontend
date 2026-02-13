package com.orwel.controller;

import com.orwel.Main;
import com.orwel.model.AuthResponse;
import com.orwel.model.LoginRequest;
import com.orwel.service.ApiService;
import com.orwel.util.AnimationUtils;
import com.orwel.util.NavigationHelper;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckbox;
    @FXML private Hyperlink forgotPasswordLink;
    @FXML private Hyperlink registerLink;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    @FXML private VBox mainContent;
    @FXML private VBox titleBox;
    @FXML private VBox loginCard;
    
    private ApiService apiService = ApiService.getInstance();
    
    @FXML
    public void initialize() {
        // Set up event handlers
        forgotPasswordLink.setOnAction(e -> handleForgotPassword());
        
        // Add animations
        Platform.runLater(() -> {
            // Staggered entrance animations
            AnimationUtils.fadeInSlideUp(titleBox, 0).play();
            AnimationUtils.fadeInSlideUp(loginCard, 200).play();
            
            // Add hover effects
            AnimationUtils.addHoverScaleEffect(loginButton);
            AnimationUtils.addNeonGlowEffect(loginButton);
        });
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }
        
        loginButton.setDisable(true);
        errorLabel.setVisible(false);
        
        // DEMO MODE: Allow login without backend for UI testing
        // Remove this when backend is ready
        if (username.equals("demo") || username.equals("test") || username.length() > 0) {
            Platform.runLater(() -> {
                // Simulate successful login for demo
                try {
                    Thread.sleep(500); // Simulate network delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                navigateToDashboard();
            });
            return;
        }
        
        // Run login in background thread
        new Thread(() -> {
            try {
                LoginRequest loginRequest = new LoginRequest(username, password);
                AuthResponse response = apiService.login(loginRequest);
                
                Platform.runLater(() -> {
                    if (response.isSuccess() && response.getToken() != null) {
                        // Save user session if remember me is checked
                        if (rememberMeCheckbox.isSelected()) {
                            // TODO: Implement session persistence
                        }
                        
                        // Navigate to dashboard
                        navigateToDashboard();
                    } else {
                        showError(response.getMessage() != null ? response.getMessage() : "Login failed. Please check your credentials.");
                        loginButton.setDisable(false);
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    // If backend is not available, allow demo mode
                    if (e.getMessage().contains("Connection") || e.getMessage().contains("refused")) {
                        showError("Backend not available. Using demo mode. Enter any username to continue.");
                        loginButton.setDisable(false);
                    } else {
                        showError("Connection error: " + e.getMessage());
                        loginButton.setDisable(false);
                    }
                });
            }
        }).start();
    }
    
    @FXML
    private void handleRegister() {
        NavigationHelper.navigate(registerLink, "/fxml/Register.fxml");
    }
    
    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText("Password Recovery");
        alert.setContentText("Please contact support or use the backend password recovery endpoint.");
        alert.showAndWait();
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private void navigateToDashboard() {
        NavigationHelper.navigateWithTransition(mainContent, "/fxml/Dashboard.fxml");
    }
}
