package com.orwel.controller;

import com.orwel.model.Country;
import com.orwel.model.NewsArticle;
import com.orwel.model.Policy;
import com.orwel.model.Stance;
import com.orwel.model.Warning;
import com.orwel.service.ApiService;
import com.orwel.util.NavigationHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

public class CountryDetailController {
    @FXML private Label countryFlagLabel;
    @FXML private Label countryNameLabel;
    @FXML private Label countryCodeLabel;
    @FXML private Label countrySummaryLabel;
    @FXML private ListView<Warning> warningsListView;
    @FXML private ListView<Policy> policiesListView;
    @FXML private ListView<Stance> stancesListView;
    @FXML private ListView<String> abidesByListView;
    @FXML private ListView<String> doesNotFollowListView;
    @FXML private ListView<NewsArticle> newsListView;
    
    private ApiService apiService = ApiService.getInstance();
    private String currentCountryCode;
    
    public void loadCountry(String countryCode) {
        this.currentCountryCode = countryCode;
        loadCountryData();
        loadWarnings();
        loadNews();
    }
    
    @FXML
    public void initialize() {
        setupListViews();
    }
    
    private void setupListViews() {
        // Warnings list view
        warningsListView.setCellFactory(new Callback<ListView<Warning>, ListCell<Warning>>() {
            @Override
            public ListCell<Warning> call(ListView<Warning> param) {
                return new ListCell<Warning>() {
                    @Override
                    protected void updateItem(Warning warning, boolean empty) {
                        super.updateItem(warning, empty);
                        if (empty || warning == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            VBox vbox = new VBox(5);
                            Label titleLabel = new Label(warning.getTitle());
                            titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c;");
                            Label messageLabel = new Label(warning.getMessage());
                            messageLabel.setWrapText(true);
                            Label severityLabel = new Label("Severity: " + warning.getSeverity().toUpperCase());
                            severityLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
                            vbox.getChildren().addAll(titleLabel, messageLabel, severityLabel);
                            setGraphic(vbox);
                        }
                    }
                };
            }
        });
        
        // Policies list view
        policiesListView.setCellFactory(new Callback<ListView<Policy>, ListCell<Policy>>() {
            @Override
            public ListCell<Policy> call(ListView<Policy> param) {
                return new ListCell<Policy>() {
                    @Override
                    protected void updateItem(Policy policy, boolean empty) {
                        super.updateItem(policy, empty);
                        if (empty || policy == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            VBox vbox = new VBox(5);
                            Label titleLabel = new Label(policy.getTitle());
                            titleLabel.setStyle("-fx-font-weight: bold;");
                            Label descLabel = new Label(policy.getDescription());
                            descLabel.setWrapText(true);
                            Label categoryLabel = new Label("Category: " + policy.getCategory());
                            categoryLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
                            vbox.getChildren().addAll(titleLabel, descLabel, categoryLabel);
                            setGraphic(vbox);
                        }
                    }
                };
            }
        });
        
        // Stances list view
        stancesListView.setCellFactory(new Callback<ListView<Stance>, ListCell<Stance>>() {
            @Override
            public ListCell<Stance> call(ListView<Stance> param) {
                return new ListCell<Stance>() {
                    @Override
                    protected void updateItem(Stance stance, boolean empty) {
                        super.updateItem(stance, empty);
                        if (empty || stance == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            VBox vbox = new VBox(5);
                            Label topicLabel = new Label(stance.getTopic());
                            topicLabel.setStyle("-fx-font-weight: bold;");
                            Label positionLabel = new Label("Position: " + stance.getPosition());
                            positionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #3498db;");
                            Label descLabel = new Label(stance.getDescription());
                            descLabel.setWrapText(true);
                            vbox.getChildren().addAll(topicLabel, positionLabel, descLabel);
                            setGraphic(vbox);
                        }
                    }
                };
            }
        });
        
        // News list view
        newsListView.setCellFactory(new Callback<ListView<NewsArticle>, ListCell<NewsArticle>>() {
            @Override
            public ListCell<NewsArticle> call(ListView<NewsArticle> param) {
                return new ListCell<NewsArticle>() {
                    @Override
                    protected void updateItem(NewsArticle article, boolean empty) {
                        super.updateItem(article, empty);
                        if (empty || article == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            VBox vbox = new VBox(5);
                            Label titleLabel = new Label(article.getTitle());
                            titleLabel.setStyle("-fx-font-weight: bold;");
                            Label summaryLabel = new Label(article.getSummary() != null ? article.getSummary() : "");
                            summaryLabel.setWrapText(true);
                            summaryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
                            vbox.getChildren().addAll(titleLabel, summaryLabel);
                            setGraphic(vbox);
                        }
                    }
                };
            }
        });
    }
    
    private void loadCountryData() {
        new Thread(() -> {
            try {
                Country country = apiService.getCountryByCode(currentCountryCode);
                if (country != null) {
                    Platform.runLater(() -> {
                        countryFlagLabel.setText(country.getFlag() != null ? country.getFlag() : "🏳️");
                        countryNameLabel.setText(country.getName());
                        countryCodeLabel.setText("Code: " + country.getCode());
                        countrySummaryLabel.setText(country.getSummary() != null ? country.getSummary() : "");
                        
                        if (country.getPolicies() != null) {
                            policiesListView.getItems().setAll(country.getPolicies());
                        }
                        
                        if (country.getStances() != null) {
                            stancesListView.getItems().setAll(country.getStances());
                        }
                        
                        if (country.getAbidesBy() != null) {
                            abidesByListView.getItems().setAll(country.getAbidesBy());
                        }
                        
                        if (country.getDoesNotFollow() != null) {
                            doesNotFollowListView.getItems().setAll(country.getDoesNotFollow());
                        }
                    });
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showError("Failed to load country data: " + e.getMessage());
                });
            }
        }).start();
    }
    
    private void loadWarnings() {
        new Thread(() -> {
            try {
                List<Warning> warnings = apiService.getWarningsForUser(currentCountryCode);
                Platform.runLater(() -> {
                    warningsListView.getItems().setAll(warnings != null ? warnings : List.of());
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    // Warnings might not be available, that's okay
                });
            }
        }).start();
    }
    
    private void loadNews() {
        new Thread(() -> {
            try {
                List<NewsArticle> news = apiService.getNewsByCountry(currentCountryCode);
                Platform.runLater(() -> {
                    newsListView.getItems().setAll(news != null ? news : List.of());
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    // News might not be available, that's okay
                });
            }
        }).start();
    }
    
    @FXML
    private void handleBack() {
        NavigationHelper.navigate(countryNameLabel, "/fxml/Countries.fxml");
    }
    
    private void showError(String message) {
        // Silent error logging - no popup
        System.err.println("[CountryDetailController] " + message);
    }
    
    // Navigation methods
    @FXML private void navigateToDashboard(javafx.event.ActionEvent event) { NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/Dashboard.fxml"); }
    @FXML private void navigateToCountries(javafx.event.ActionEvent event) { NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/Countries.fxml"); }
    @FXML private void navigateToNews(javafx.event.ActionEvent event) { NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/News.fxml"); }
    @FXML private void navigateToProfile(javafx.event.ActionEvent event) { NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/Profile.fxml"); }
    @FXML private void navigateToAbout(javafx.event.ActionEvent event) { NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/About.fxml"); }
    @FXML private void handleLogout(javafx.event.ActionEvent event) {
        apiService.logout();
        NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/Login.fxml");
    }
}
