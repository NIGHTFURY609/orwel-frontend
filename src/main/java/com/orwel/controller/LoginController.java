package com.orwel.controller;

import java.io.IOException;

import com.orwel.config.AppConfig;
import com.orwel.model.AuthResponse;
import com.orwel.model.LoginRequest;
import com.orwel.service.ApiService;
import com.orwel.util.AnimationUtils;
import com.orwel.util.NavigationHelper;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class LoginController {
    // Login fields
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckbox;
    @FXML private Hyperlink forgotPasswordLink;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    
    // Signup fields
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField signupEmailField;
    @FXML private TextField signupUsernameField;
    @FXML private PasswordField signupPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button signupButton;
    @FXML private Label signupErrorLabel;
    
    // UI Elements
    @FXML private VBox mainContent;
    @FXML private VBox titleBox;
    @FXML private VBox loginCard;
    @FXML private VBox signupCard;
    @FXML private Text titleText;
    @FXML private Button loginTabButton;
    @FXML private Button signupTabButton;
    @FXML private Hyperlink toSignupLink;
    @FXML private Hyperlink toLoginLink;
    @FXML private Hyperlink backHomeLink;
    
    private ApiService apiService = ApiService.getInstance();
    
    @FXML
    public void initialize() {
        // Load configuration text
        titleText.setText(AppConfig.APP_TITLE);
        
        // Load background image
        loadBackgroundImage();
        
        // Add animations on load
        Platform.runLater(() -> {
            AnimationUtils.fadeInSlideUp(titleBox, 0).play();
            AnimationUtils.fadeInSlideUp(loginCard, 200).play();
            
            // Add hover effects
            AnimationUtils.addHoverScaleEffect(loginButton);
            AnimationUtils.addHoverScaleEffect(signupButton);
            AnimationUtils.addNeonGlowEffect(loginButton);
            AnimationUtils.addNeonGlowEffect(signupButton);
        });
    }
    
    private void loadBackgroundImage() {
        try {
            String imageUrl = getClass().getResource("/images/loginpage.jpg").toExternalForm();
            Image backgroundImage = new Image(imageUrl, true);
            
            BackgroundImage bg = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, false)
            );
            
            javafx.scene.layout.Background background = new javafx.scene.layout.Background(bg);
            mainContent.setBackground(background);
        } catch (Exception e) {
            System.err.println("Failed to load background image: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleLoginTab() {
        // Show login card, hide signup card
        loginTabButton.setStyle("-fx-background-color: #00D4FF; -fx-text-fill: #05070A;");
        signupTabButton.setStyle("-fx-background-color: rgba(0, 242, 255, 0.1); -fx-text-fill: #00F2FF;");
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), signupCard);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            signupCard.setVisible(false);
            signupCard.setManaged(false);
        });
        fadeOut.play();
        
        loginCard.setManaged(true);
        loginCard.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), loginCard);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    @FXML
    private void handleSignupTab() {
        // Show signup card, hide login card
        signupTabButton.setStyle("-fx-background-color: #00D4FF; -fx-text-fill: #05070A;");
        loginTabButton.setStyle("-fx-background-color: rgba(0, 242, 255, 0.1); -fx-text-fill: #00F2FF;");
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), loginCard);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            loginCard.setVisible(false);
            loginCard.setManaged(false);
        });
        fadeOut.play();
        
        signupCard.setManaged(true);
        signupCard.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), signupCard);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showLoginError("Please enter both username and password");
            return;
        }
        
        loginButton.setDisable(true);
        errorLabel.setVisible(false);
        
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
                        showLoginError(response.getMessage() != null ? response.getMessage() : "Login failed. Please check your credentials.");
                        loginButton.setDisable(false);
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    // If backend is not available, show demo mode hint
                    if (e.getMessage().contains("Connection") || e.getMessage().contains("refused")) {
                        showLoginError("Backend not available. Demo mode enabled - Enter any username to continue.");
                        loginButton.setDisable(false);
                        
                        // Allow login with any credentials when backend is down
                        if (!username.isEmpty()) {
                            navigateToDashboard();
                        }
                    } else {
                        showLoginError("Connection error: " + e.getMessage());
                        loginButton.setDisable(false);
                    }
                });
            }
        }).start();
    }
    
    @FXML
    private void handleSignup() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = signupEmailField.getText().trim();
        String username = signupUsernameField.getText().trim();
        String password = signupPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Validation
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showSignupError("Please fill in all fields");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showSignupError("Passwords do not match");
            return;
        }
        
        if (password.length() < 6) {
            showSignupError("Password must be at least 6 characters");
            return;
        }
        
        if (!email.contains("@")) {
            showSignupError("Please enter a valid email address");
            return;
        }
        
        signupButton.setDisable(true);
        signupErrorLabel.setVisible(false);
        
        // For now, just show a message and allow proceeding to dashboard in demo mode
        showSignupError("Account creation via backend not yet implemented. Using demo mode.");
        signupButton.setDisable(false);
        
        // In demo mode, navigate to dashboard after showing message
        Platform.runLater(() -> {
            try {
                Thread.sleep(1000);
                navigateToDashboard();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
    
    @FXML
    private void handleBackHome() {
        if (backHomeLink != null) {
            NavigationHelper.navigate(backHomeLink, "/fxml/Landing.fxml");
        }
    }
    
    private void showLoginError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private void showSignupError(String message) {
        signupErrorLabel.setText(message);
        signupErrorLabel.setVisible(true);
    }
    
    private void navigateToDashboard() {
        NavigationHelper.navigateWithTransition(mainContent, "/fxml/Dashboard.fxml");
    }
}
