package com.orwel.controller;

import com.orwel.config.AppConfig;
import com.orwel.util.AnimationUtils;
import com.orwel.util.NavigationHelper;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LandingController {
    @FXML private VBox heroSection;
    @FXML private VBox heroContent;
    @FXML private VBox featuresSection;
    @FXML private VBox benefitsSection;
    @FXML private VBox ctaSection;
    @FXML private VBox feature1Card;
    @FXML private VBox feature2Card;
    @FXML private VBox feature3Card;
    @FXML private Text heroTitle;
    @FXML private Text heroSubtitle;
    @FXML private Text heroTagline;
    @FXML private Button getStartedButton;
    @FXML private Button learnMoreButton;
    @FXML private Button loginNavButton;
    @FXML private Button signupNavButton;
    
    @FXML
    public void initialize() {
        // Set dynamic text from config
        heroTitle.setText(AppConfig.APP_TITLE);
        heroSubtitle.setText(AppConfig.APP_SUBTITLE);
        heroTagline.setText(AppConfig.APP_TAGLINE);
        
        // Load background image
        loadBackgroundImage();
        
        // Add animations on load
        Platform.runLater(this::setupAnimations);
    }
    
    private void loadBackgroundImage() {
        try {
            String imageUrl = getClass().getResource("/images/landingpage.jpg").toExternalForm();
            Image backgroundImage = new Image(imageUrl, true);
            
            BackgroundImage bg = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, false)
            );
            
            heroSection.setStyle("-fx-background-color: #0F1115;");
            javafx.scene.layout.Background background = new javafx.scene.layout.Background(bg);
            heroSection.setBackground(background);
        } catch (Exception e) {
            System.err.println("Failed to load background image: " + e.getMessage());
        }
    }
    
    private void setupAnimations() {
        // Fade in hero content
        AnimationUtils.fadeInSlideUp(heroContent, 0).play();
        
        // Stagger feature cards entrance
        AnimationUtils.staggerFadeInSlideUp(
            java.util.Arrays.asList(feature1Card, feature2Card, feature3Card),
            150
        );
        
        // Pulse the main CTA button
        addPulseEffect(getStartedButton);
        
        // Add hover effects to all cards
        AnimationUtils.addHoverScaleEffect(feature1Card);
        AnimationUtils.addHoverScaleEffect(feature2Card);
        AnimationUtils.addHoverScaleEffect(feature3Card);
        AnimationUtils.addHoverScaleEffect(getStartedButton);
        AnimationUtils.addHoverScaleEffect(learnMoreButton);
    }
    
    private void addPulseEffect(Button button) {
        // Create a subtle pulse animation
        AnimationUtils.addNeonGlowEffect(button);
    }
    
    @FXML
    private void handleGetStarted() {
        if (getStartedButton != null) {
            NavigationHelper.navigate(getStartedButton, "/fxml/Login.fxml");
        }
    }
    
    @FXML
    private void handleLearnMore() {
        // Scroll to features section (or show more info)
        // For now, navigate to the main dashboard to explore
        if (learnMoreButton != null) {
            NavigationHelper.navigate(learnMoreButton, "/fxml/Login.fxml");
        }
    }
    
    @FXML
    private void handleLoginNav() {
        if (loginNavButton != null) {
            NavigationHelper.navigate(loginNavButton, "/fxml/Login.fxml");
        }
    }
    
    @FXML
    private void handleSignupNav() {
        if (signupNavButton != null) {
            NavigationHelper.navigate(signupNavButton, "/fxml/Register.fxml");
        }
    }
}
