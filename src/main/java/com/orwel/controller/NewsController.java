package com.orwel.controller;

import com.orwel.model.Country;
import com.orwel.model.NewsArticle;
import com.orwel.service.ApiService;
import com.orwel.util.NavigationHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class NewsController {
    @FXML private Label newsTitleLabel;
    @FXML private ListView<NewsArticle> newsListView;
    @FXML private ToggleGroup newsTypeGroup;
    @FXML private RadioButton personalizedRadio;
    @FXML private RadioButton generalRadio;
    @FXML private ComboBox<String> regionComboBox;
    @FXML private ComboBox<String> countryComboBox;
    @FXML private TextField searchField;
    
    private ApiService apiService = ApiService.getInstance();
    
    @FXML
    public void initialize() {
        setupListView();
        setupFilters();
        loadPersonalizedNews();
    }
    
    private void setupListView() {
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
                            VBox vbox = new VBox(10);
                            vbox.setStyle("-fx-padding: 15;");
                            
                            Label titleLabel = new Label(article.getTitle());
                            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                            titleLabel.setWrapText(true);
                            
                            Label summaryLabel = new Label(article.getSummary() != null ? article.getSummary() : "");
                            summaryLabel.setWrapText(true);
                            summaryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
                            
                            HBox metaBox = new HBox(10);
                            if (article.getSource() != null) {
                                Label sourceLabel = new Label("Source: " + article.getSource());
                                sourceLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #95a5a6;");
                                metaBox.getChildren().add(sourceLabel);
                            }
                            if (article.getPublishedAt() != null) {
                                Label dateLabel = new Label(article.getPublishedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
                                dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #95a5a6;");
                                metaBox.getChildren().add(dateLabel);
                            }
                            if (article.getCountryCode() != null) {
                                Label countryLabel = new Label("Country: " + article.getCountryCode());
                                countryLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #95a5a6;");
                                metaBox.getChildren().add(countryLabel);
                            }
                            
                            if (article.getRelevanceScore() != null && article.getRelevanceScore() > 0) {
                                Label relevanceLabel = new Label("Relevance: " + String.format("%.0f%%", article.getRelevanceScore() * 100));
                                relevanceLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
                                metaBox.getChildren().add(relevanceLabel);
                            }
                            
                            vbox.getChildren().addAll(titleLabel, summaryLabel, metaBox);
                            setGraphic(vbox);
                            
                            setOnMouseClicked(e -> {
                                if (e.getClickCount() == 2 && article.getUrl() != null) {
                                    // Open URL in browser
                                    try {
                                        java.awt.Desktop.getDesktop().browse(new java.net.URI(article.getUrl()));
                                    } catch (Exception ex) {
                                        // Could not open browser
                                    }
                                }
                            });
                        }
                    }
                };
            }
        });
    }
    
    private void setupFilters() {
        List<String> regions = Arrays.asList(
            "All Regions", "North America", "South America", "Europe", "Asia", 
            "Africa", "Oceania", "Middle East"
        );
        regionComboBox.setItems(FXCollections.observableArrayList(regions));
        regionComboBox.setValue("All Regions");
        
        // Load countries
        new Thread(() -> {
            try {
                List<Country> countries = apiService.getAllCountries();
                if (countries != null) {
                    Platform.runLater(() -> {
                        List<String> countryNames = countries.stream()
                            .map(Country::getName)
                            .collect(java.util.stream.Collectors.toList());
                        countryNames.add(0, "All Countries");
                        countryComboBox.setItems(FXCollections.observableArrayList(countryNames));
                        countryComboBox.setValue("All Countries");
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    @FXML
    private void handleFilterChange() {
        if (personalizedRadio.isSelected()) {
            loadPersonalizedNews();
        } else {
            loadGeneralNews();
        }
    }
    
    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            handleFilterChange();
            return;
        }
        
        String countryValue = countryComboBox.getValue();
        String regionValue = regionComboBox.getValue();
        
        final String country = ("All Countries".equals(countryValue)) ? null : countryValue;
        final String region = ("All Regions".equals(regionValue)) ? null : regionValue;
        
        new Thread(() -> {
            try {
                List<NewsArticle> articles = apiService.searchNews(query, country, region);
                Platform.runLater(() -> {
                    newsTitleLabel.setText("Search Results");
                    newsListView.getItems().setAll(articles != null ? articles : List.of());
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showError("Search failed: " + e.getMessage());
                });
            }
        }).start();
    }
    
    private void loadPersonalizedNews() {
        newsTitleLabel.setText("Personalized News");
        new Thread(() -> {
            try {
                List<NewsArticle> articles = apiService.getPersonalizedNews();
                Platform.runLater(() -> {
                    newsListView.getItems().setAll(articles != null ? articles : List.of());
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showError("Failed to load personalized news: " + e.getMessage());
                });
            }
        }).start();
    }
    
    private void loadGeneralNews() {
        newsTitleLabel.setText("General News");
        new Thread(() -> {
            try {
                List<NewsArticle> articles = apiService.getGeneralNews();
                Platform.runLater(() -> {
                    newsListView.getItems().setAll(articles != null ? articles : List.of());
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showError("Failed to load general news: " + e.getMessage());
                });
            }
        }).start();
    }
    
    private void showError(String message) {
        // Silent error logging - no popup
        System.err.println("[NewsController] " + message);
    }
    
    // Navigation methods
    @FXML private void navigateToDashboard(javafx.event.ActionEvent event) { NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/Dashboard.fxml"); }
    @FXML private void navigateToCountries(javafx.event.ActionEvent event) { NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/Countries.fxml"); }
    @FXML private void navigateToNews(javafx.event.ActionEvent event) { /* Already on news */ }
    @FXML private void navigateToProfile(javafx.event.ActionEvent event) { NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/Profile.fxml"); }
    @FXML private void navigateToAbout(javafx.event.ActionEvent event) { NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/About.fxml"); }
    @FXML private void handleLogout(javafx.event.ActionEvent event) {
        apiService.logout();
        NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/Login.fxml");
    }
}
