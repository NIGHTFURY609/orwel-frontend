package com.orwel.controller;

import com.orwel.model.*;
import com.orwel.service.ApiService;
import com.orwel.util.NavigationHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UpdatesController {
    @FXML private Label updatesTitleLabel;
    @FXML private ListView<Object> updatesListView;
    @FXML private RadioButton legislationRadio;
    @FXML private RadioButton hearingsRadio;
    @FXML private RadioButton nominationsRadio;
    @FXML private RadioButton allUpdatesRadio;
    @FXML private ToggleGroup updateTypeGroup;
    @FXML private Label statusLabel;
    
    private ApiService apiService = ApiService.getInstance();
    private List<String> userTags;
    
    @FXML
    public void initialize() {
        setupListView();
        loadUserTagsAndData();
    }
    
    private void setupListView() {
        updatesListView.setCellFactory(param -> new ListCell<Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox(10);
                    vbox.setStyle("-fx-padding: 15;");
                    
                    if (item instanceof Legislation) {
                        Legislation leg = (Legislation) item;
                        Label typeLabel = new Label("📜 LEGISLATION");
                        typeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #3498db; -fx-font-weight: bold;");
                        Label titleLabel = new Label(leg.getRefCode() + ": " + leg.getTitle());
                        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                        titleLabel.setWrapText(true);
                        Label summaryLabel = new Label(leg.getSummary() != null ? leg.getSummary() : "");
                        summaryLabel.setWrapText(true);
                        summaryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
                        Label dateLabel = new Label("Introduced: " + (leg.getDateIntroduced() != null ? 
                            leg.getDateIntroduced().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "Unknown"));
                        dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #95a5a6;");
                        Label statusLabel = new Label("Status: " + leg.getCurrentStatus());
                        statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #27ae60;");
                        vbox.getChildren().addAll(typeLabel, titleLabel, summaryLabel, dateLabel, statusLabel);
                        
                    } else if (item instanceof CommitteeMaterial) {
                        CommitteeMaterial hearing = (CommitteeMaterial) item;
                        Label typeLabel = new Label("🎙️ HEARING");
                        typeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                        Label titleLabel = new Label(hearing.getTitle());
                        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                        titleLabel.setWrapText(true);
                        Label summaryLabel = new Label(hearing.getOfficialSummary() != null ? hearing.getOfficialSummary() : "");
                        summaryLabel.setWrapText(true);
                        summaryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
                        Label dateLabel = new Label("Date: " + (hearing.getEventDate() != null ? 
                            hearing.getEventDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "TBD"));
                        dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #95a5a6;");
                        vbox.getChildren().addAll(typeLabel, titleLabel, summaryLabel, dateLabel);
                        
                    } else if (item instanceof Nomination) {
                        Nomination nom = (Nomination) item;
                        Label typeLabel = new Label("👤 NOMINATION");
                        typeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #f39c12; -fx-font-weight: bold;");
                        String memberName = nom.getMember() != null ? nom.getMember().getFullName() : "Unknown";
                        Label titleLabel = new Label(memberName + " - " + nom.getPositionTitle());
                        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                        titleLabel.setWrapText(true);
                        Label orgLabel = new Label(nom.getTargetOrganization());
                        orgLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
                        Label summaryLabel = new Label(nom.getOfficialSummary() != null ? nom.getOfficialSummary() : "");
                        summaryLabel.setWrapText(true);
                        summaryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
                        Label statusLabel = new Label("Status: " + nom.getCurrentStatus());
                        statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #27ae60;");
                        vbox.getChildren().addAll(typeLabel, titleLabel, orgLabel, summaryLabel, statusLabel);
                        
                    } else if (item instanceof Treaty) {
                        Treaty treaty = (Treaty) item;
                        Label typeLabel = new Label("🤝 TREATY");
                        typeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #9b59b6; -fx-font-weight: bold;");
                        Label titleLabel = new Label(treaty.getTitle());
                        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                        titleLabel.setWrapText(true);
                        Label partnerLabel = new Label("Partner: " + treaty.getForeignPartner());
                        partnerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
                        Label summaryLabel = new Label(treaty.getOfficialSummary() != null ? treaty.getOfficialSummary() : "");
                        summaryLabel.setWrapText(true);
                        summaryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
                        Label statusLabel = new Label("Status: " + treaty.getCurrentStatus());
                        statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #27ae60;");
                        vbox.getChildren().addAll(typeLabel, titleLabel, partnerLabel, summaryLabel, statusLabel);
                    }
                    
                    setGraphic(vbox);
                }
            }
        });
    }
    
    private void loadUserTagsAndData() {
        new Thread(() -> {
            try {
                User user = apiService.getCurrentUser();
                if (user != null && user.getCommodityTags() != null && !user.getCommodityTags().isEmpty()) {
                    userTags = user.getCommodityTags();
                    Platform.runLater(() -> {
                        loadData();
                    });
                } else {
                    Platform.runLater(() -> {
                        statusLabel.setText("Please add commodity tags in your profile to see relevant updates");
                        statusLabel.setVisible(true);
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Failed to load user data: " + e.getMessage());
                    statusLabel.setVisible(true);
                });
            }
        }).start();
    }
    
    @FXML
    private void handleFilterChange() {
        loadData();
    }
    
    private void loadData() {
        if (userTags == null || userTags.isEmpty()) {
            return;
        }
        
        statusLabel.setText("Loading updates...");
        statusLabel.setVisible(true);
        
        new Thread(() -> {
            try {
                List<Object> updates = new ArrayList<>();
                
                if (allUpdatesRadio != null && allUpdatesRadio.isSelected()) {
                    // Load all types
                    List<Legislation> legislation = apiService.getLegislationByTags(userTags);
                    if (legislation != null) updates.addAll(legislation);
                    
                    List<CommitteeMaterial> hearings = apiService.getHearingsByTags(userTags);
                    if (hearings != null) updates.addAll(hearings);
                    
                    List<Nomination> nominations = apiService.getNominationsByTags(userTags);
                    if (nominations != null) updates.addAll(nominations);
                    
                    List<Treaty> treaties = apiService.getTreatiesByTags(userTags);
                    if (treaties != null) updates.addAll(treaties);
                    
                } else if (legislationRadio != null && legislationRadio.isSelected()) {
                    List<Legislation> legislation = apiService.getLegislationByTags(userTags);
                    if (legislation != null) updates.addAll(legislation);
                    
                } else if (hearingsRadio != null && hearingsRadio.isSelected()) {
                    List<CommitteeMaterial> hearings = apiService.getHearingsByTags(userTags);
                    if (hearings != null) updates.addAll(hearings);
                    
                } else if (nominationsRadio != null && nominationsRadio.isSelected()) {
                    List<Nomination> nominations = apiService.getNominationsByTags(userTags);
                    if (nominations != null) updates.addAll(nominations);
                }
                
                Platform.runLater(() -> {
                    updatesListView.getItems().setAll(updates);
                    statusLabel.setVisible(false);
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Failed to load updates: " + e.getMessage());
                    statusLabel.setVisible(true);
                });
            }
        }).start();
    }
    
    // Navigation methods
    @FXML
    private void navigateToDashboard(javafx.event.ActionEvent event) { 
        NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/Dashboard.fxml"); 
    }
    
    @FXML
    private void navigateToCountries(javafx.event.ActionEvent event) { 
        NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/Countries.fxml"); 
    }
    
    @FXML
    private void navigateToNews(javafx.event.ActionEvent event) { 
        /* Already on Updates */ 
    }
    
    @FXML
    private void navigateToUpdates(javafx.event.ActionEvent event) { 
        /* Already on Updates */ 
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
