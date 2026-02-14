package com.orwel.controller;

import java.io.IOException;
import java.sql.SQLException;

import com.orwel.config.AppConfig;
import com.orwel.model.AuthResponse;
import com.orwel.model.LoginRequest;
import com.orwel.model.User;
import com.orwel.service.ApiService;
import com.orwel.service.UserDatabase;
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
        String usernameOrEmail = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            showLoginError("Please enter both username/email and password");
            return;
        }
        
        loginButton.setDisable(true);
        errorLabel.setVisible(false);
        
        // Run login in background thread
        new Thread(() -> {
            try {
                // Try backend login first
                LoginRequest loginRequest = new LoginRequest(usernameOrEmail, password);
                AuthResponse response = apiService.login(loginRequest);
                
                Platform.runLater(() -> {
                    if (response.isSuccess() && response.getToken() != null) {
                        // Navigate to dashboard
                        navigateToDashboard();
                    } else {
                        showLoginError(response.getMessage() != null ? response.getMessage() : "Login failed. Please check your credentials.");
                        loginButton.setDisable(false);
                    }
                });
            } catch (IOException e) {
                // Run in separate thread to avoid blocking
                new Thread(() -> {
                    // If backend is not available, try SQLite authentication
                    System.out.println("Backend unavailable, trying local authentication...");
                    System.out.println("Looking for user: " + usernameOrEmail);
                    
                    try {
                        User localUser = null;
                        
                        // Try to find user by email or username
                        if (usernameOrEmail.contains("@")) {
                            localUser = UserDatabase.getUserByEmail(usernameOrEmail);
                            System.out.println("Searched by email, found: " + (localUser != null));
                        } else {
                            localUser = UserDatabase.getUserByUsername(usernameOrEmail);
                            System.out.println("Searched by username, found: " + (localUser != null));
                        }
                        
                        if (localUser != null) {
                            System.out.println("User found. Checking password...");
                            System.out.println("Stored password: " + localUser.getPassword());
                            System.out.println("Entered password: " + password);
                            
                            if (localUser.getPassword().equals(password)) {
                                // Successful local authentication
                                System.out.println("Password match! Logging in...");
                                apiService.setCurrentUser(localUser);
                                
                                Platform.runLater(() -> {
                                    showLoginError("✓ Offline login successful!");
                                    errorLabel.setStyle("-fx-text-fill: #00FF00;"); // Green
                                    
                                    // Navigate after delay
                                    new Thread(() -> {
                                        try {
                                            Thread.sleep(1000);
                                            Platform.runLater(() -> navigateToDashboard());
                                        } catch (InterruptedException ie) {
                                            Thread.currentThread().interrupt();
                                        }
                                    }).start();
                                });
                            } else {
                                System.out.println("Password mismatch!");
                                Platform.runLater(() -> {
                                    showLoginError("Incorrect password");
                                    loginButton.setDisable(false);
                                });
                            }
                        } else {
                            System.out.println("User not found in database");
                            Platform.runLater(() -> {
                                showLoginError("User not found. Please sign up first.");
                                loginButton.setDisable(false);
                            });
                        }
                    } catch (SQLException sqlEx) {
                        sqlEx.printStackTrace();
                        Platform.runLater(() -> {
                            showLoginError("Database error: " + sqlEx.getMessage());
                            loginButton.setDisable(false);
                        });
                    }
                }).start();
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
        
        // Run signup in background thread
        new Thread(() -> {
            try {
                // Check if user already exists in SQLite
                if (UserDatabase.userExists(email)) {
                    Platform.runLater(() -> {
                        showSignupError("User with this email already exists locally");
                        signupButton.setDisable(false);
                    });
                    return;
                }
                
                // Create new user
                User newUser = new User();
                newUser.setFirstName(firstName);
                newUser.setLastName(lastName);
                newUser.setEmail(email);
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.setHasStocks(false);
                
                // Try to register with backend
                try {
                    AuthResponse response = apiService.register(newUser);
                    
                    Platform.runLater(() -> {
                        if (response.isSuccess()) {
                            showSignupError("✓ Registration successful! Switching to login...");
                            signupErrorLabel.setStyle("-fx-text-fill: #00FF00;"); // Green for success
                            new Thread(() -> {
                                try {
                                    Thread.sleep(1500);
                                    Platform.runLater(() -> {
                                        signupErrorLabel.setStyle("-fx-text-fill: #FF0000;"); // Reset to red
                                        handleLoginTab();
                                    });
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }).start();
                        } else {
                            showSignupError(response.getMessage() != null ? response.getMessage() : "Registration failed");
                            signupButton.setDisable(false);
                        }
                    });
                } catch (IOException e) {
                    // Backend unavailable, save to SQLite only
                    System.out.println("Backend unavailable, saving user locally...");
                    
                    UserDatabase.saveUser(newUser);
                    apiService.setCurrentUser(newUser);
                    
                    Platform.runLater(() -> {
                        showSignupError("✓ Account saved locally! Switching to login...");
                        signupErrorLabel.setStyle("-fx-text-fill: #00FF00;"); // Green for success
                        new Thread(() -> {
                            try {
                                Thread.sleep(1500);
                                Platform.runLater(() -> {
                                    signupErrorLabel.setStyle("-fx-text-fill: #FF0000;"); // Reset to red
                                    handleLoginTab();
                                });
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                            }
                        }).start();
                    });
                }
            } catch (SQLException sqlEx) {
                Platform.runLater(() -> {
                    showSignupError("Database error: " + sqlEx.getMessage());
                    signupButton.setDisable(false);
                });
            }
        }).start();
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
