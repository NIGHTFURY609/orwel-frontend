package com.orwel.controller;

import com.orwel.model.*;
import com.orwel.service.ApiService;
import com.orwel.util.AnimationUtils;
import com.orwel.util.NavigationHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label welcomeDescriptionLabel;
    @FXML private Label legislationCountLabel;
    @FXML private Label hearingsCountLabel;
    @FXML private Label nominationsCountLabel;
    @FXML private Label committeesCountLabel;
    @FXML private VBox legislationCard;
    @FXML private VBox hearingsCard;
    @FXML private VBox nominationsCard;
    @FXML private VBox committeesCard;
    @FXML private VBox welcomeCard;
    @FXML private VBox legislationCardSection;
    @FXML private VBox hearingsCardSection;
    @FXML private VBox nominationsCardSection;
    @FXML private VBox committeesCardSection;
    @FXML private VBox dashboardContent;
    @FXML private ListView<Legislation> legislationListView;
    @FXML private ListView<CommitteeMaterial> hearingsListView;
    @FXML private ListView<Nomination> nominationsListView;
    @FXML private ListView<Committee> committeesListView;
    
    private ApiService apiService = ApiService.getInstance();
    
    @FXML
    public void initialize() {
        // Set welcome description from config
        if (welcomeDescriptionLabel != null) {
            welcomeDescriptionLabel.setText("Track government legislation, hearings, and nominations affecting your commodity interests");
        }
        
        // Initialize counts to 0 immediately
        if (legislationCountLabel != null) legislationCountLabel.setText("0");
        if (hearingsCountLabel != null) hearingsCountLabel.setText("0");
        if (nominationsCountLabel != null) nominationsCountLabel.setText("0");
        if (committeesCountLabel != null) committeesCountLabel.setText("0");
        
        setupListViews();
        loadDashboardData();
        addAnimations();
    }
    
    private void addAnimations() {
        Platform.runLater(() -> {
            // Build list of non-null cards for animation
            List<javafx.scene.Node> cardsToAnimate = new ArrayList<>();
            if (welcomeCard != null) cardsToAnimate.add(welcomeCard);
            if (legislationCard != null) cardsToAnimate.add(legislationCard);
            if (hearingsCard != null) cardsToAnimate.add(hearingsCard);
            if (nominationsCard != null) cardsToAnimate.add(nominationsCard);
            if (committeesCard != null) cardsToAnimate.add(committeesCard);
            if (legislationCardSection != null) cardsToAnimate.add(legislationCardSection);
            if (hearingsCardSection != null) cardsToAnimate.add(hearingsCardSection);
            if (nominationsCardSection != null) cardsToAnimate.add(nominationsCardSection);
            if (committeesCardSection != null) cardsToAnimate.add(committeesCardSection);
            
            // Staggered entrance animations for all cards
            if (!cardsToAnimate.isEmpty()) {
                AnimationUtils.staggerFadeInSlideUp(cardsToAnimate, 100);
            }
            
            // Add hover effects to stat cards (if they exist)
            if (legislationCard != null) AnimationUtils.addHoverScaleEffect(legislationCard);
            if (hearingsCard != null) AnimationUtils.addHoverScaleEffect(hearingsCard);
            if (nominationsCard != null) AnimationUtils.addHoverScaleEffect(nominationsCard);
            if (committeesCard != null) AnimationUtils.addHoverScaleEffect(committeesCard);
            
            // Add neon glow to label
            if (welcomeLabel != null) AnimationUtils.addNeonGlowEffect(welcomeLabel);
        });
    }
    
    private void setupListViews() {
        // Setup legislation list view (simplified for performance)
        if (legislationListView != null) {
            legislationListView.setCellFactory(param -> new ListCell<Legislation>() {
                @Override
                protected void updateItem(Legislation leg, boolean empty) {
                    super.updateItem(leg, empty);
                    if (empty || leg == null) {
                        setText(null);
                    } else {
                        setText(leg.getRefCode() + ": " + leg.getTitle() + "\nStatus: " + leg.getCurrentStatus());
                    }
                }
            });
        }
        
        // Setup hearings list view (simplified for performance)
        if (hearingsListView != null) {
            hearingsListView.setCellFactory(param -> new ListCell<CommitteeMaterial>() {
                @Override
                protected void updateItem(CommitteeMaterial hearing, boolean empty) {
                    super.updateItem(hearing, empty);
                    if (empty || hearing == null) {
                        setText(null);
                    } else {
                        String date = hearing.getEventDate() != null ? 
                            hearing.getEventDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "";
                        setText(hearing.getTitle() + (date.isEmpty() ? "" : "\nDate: " + date));
                    }
                }
            });
        }
        
        // Setup nominations list view (simplified for performance)
        if (nominationsListView != null) {
            nominationsListView.setCellFactory(param -> new ListCell<Nomination>() {
                @Override
                protected void updateItem(Nomination nom, boolean empty) {
                    super.updateItem(nom, empty);
                    if (empty || nom == null) {
                        setText(null);
                    } else {
                        String memberName = nom.getMember() != null ? nom.getMember().getFullName() : "Unknown";
                        setText(memberName + " - " + nom.getPositionTitle() + "\n" + nom.getTargetOrganization());
                    }
                }
            });
        }
        
        // Setup committees list view (simplified for performance)
        if (committeesListView != null) {
            committeesListView.setCellFactory(param -> new ListCell<Committee>() {
                @Override
                protected void updateItem(Committee committee, boolean empty) {
                    super.updateItem(committee, empty);
                    if (empty || committee == null) {
                        setText(null);
                    } else {
                        String code = committee.getOfficialCode() != null ? 
                            "\nCode: " + committee.getOfficialCode() : "";
                        setText(committee.getName() + code);
                    }
                }
            });
        }
    }
    
    private void loadDashboardData() {
        // Load user info synchronously first
        User user = apiService.getCurrentUser();
        if (user != null && user.getFirstName() != null) {
            if (welcomeLabel != null) {
                welcomeLabel.setText("Welcome, " + user.getFirstName() + "!");
            }
            
            // Load data based on user tags (only if we have tags)
            if (user.getCommodityTags() != null && !user.getCommodityTags().isEmpty()) {
                // Load all data in a single background thread to reduce overhead
                new Thread(() -> {
                    try {
                        // Try to load legislation
                        try {
                            List<Legislation> legislation = apiService.getLegislationByTags(user.getCommodityTags());
                            if (legislation != null && !legislation.isEmpty()) {
                                Platform.runLater(() -> {
                                    if (legislationCountLabel != null) legislationCountLabel.setText(String.valueOf(legislation.size()));
                                    if (legislationListView != null) legislationListView.getItems().setAll(legislation.subList(0, Math.min(5, legislation.size())));
                                });
                            }
                        } catch (Exception e) { /* Offline - already set to 0 */ }
                        
                        // Try to load hearings
                        try {
                            List<CommitteeMaterial> hearings = apiService.getHearingsByTags(user.getCommodityTags());
                            if (hearings != null && !hearings.isEmpty()) {
                                Platform.runLater(() -> {
                                    if (hearingsCountLabel != null) hearingsCountLabel.setText(String.valueOf(hearings.size()));
                                    if (hearingsListView != null) hearingsListView.getItems().setAll(hearings.subList(0, Math.min(5, hearings.size())));
                                });
                            }
                        } catch (Exception e) { /* Offline - already set to 0 */ }
                        
                        // Try to load nominations
                        try {
                            List<Nomination> nominations = apiService.getNominationsByTags(user.getCommodityTags());
                            if (nominations != null && !nominations.isEmpty()) {
                                Platform.runLater(() -> {
                                    if (nominationsCountLabel != null) nominationsCountLabel.setText(String.valueOf(nominations.size()));
                                    if (nominationsListView != null) nominationsListView.getItems().setAll(nominations.subList(0, Math.min(5, nominations.size())));
                                });
                            }
                        } catch (Exception e) { /* Offline - already set to 0 */ }
                        
                        // Try to load committees
                        try {
                            List<Committee> committees = apiService.getCommitteesByTags(user.getCommodityTags());
                            if (committees != null && !committees.isEmpty()) {
                                Platform.runLater(() -> {
                                    if (committeesCountLabel != null) committeesCountLabel.setText(String.valueOf(committees.size()));
                                    if (committeesListView != null) committeesListView.getItems().setAll(committees.subList(0, Math.min(5, committees.size())));
                                });
                            }
                        } catch (Exception e) { /* Offline - already set to 0 */ }
                        
                    } catch (Exception e) {
                        System.err.println("Error loading dashboard data: " + e.getMessage());
                    }
                }).start();
            } else {
                if (welcomeDescriptionLabel != null) {
                    welcomeDescriptionLabel.setText("Please add commodity tags in your profile to see relevant legislation");
                }
            }
        } else {
            if (welcomeLabel != null) {
                welcomeLabel.setText("Welcome!");
            }
        }
    }
    
    
    @FXML
    private void navigateToDashboard() {
        // Already on dashboard
    }
    
    @FXML
    private void navigateToCountries(javafx.event.ActionEvent event) {
        NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/Countries.fxml");
    }
    
    @FXML
    private void navigateToNews(javafx.event.ActionEvent event) {
        NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/News.fxml");
    }
    
    @FXML
    private void navigateToUpdates(javafx.event.ActionEvent event) {
        NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/News.fxml");
    }
    
    @FXML
    private void navigateToProfile(javafx.event.ActionEvent event) {
        NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/Profile.fxml");
    }
    
    @FXML
    private void navigateToAbout(javafx.event.ActionEvent event) {
        NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/About.fxml");
    }
    
    @FXML
    private void handleLogout(javafx.event.ActionEvent event) {
        apiService.logout();
        NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/Login.fxml");
    }
}
