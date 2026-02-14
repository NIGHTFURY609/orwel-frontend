package com.orwel.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.orwel.model.Country;
import com.orwel.service.ApiService;
import com.orwel.util.NavigationHelper;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class CountriesController {
    @FXML private FlowPane countriesFlowPane;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> regionFilterComboBox;
    
    private ApiService apiService = ApiService.getInstance();
    private List<Country> allCountries;
    
    @FXML
    public void initialize() {
        setupRegionFilter();
        loadCountries();
    }
    
    private void setupRegionFilter() {
        List<String> regions = Arrays.asList(
            "All Regions", "North America", "South America", "Europe", "Asia", 
            "Africa", "Oceania", "Middle East"
        );
        regionFilterComboBox.setItems(FXCollections.observableArrayList(regions));
        regionFilterComboBox.setValue("All Regions");
    }
    
    private void loadCountries() {
        new Thread(() -> {
            try {
                allCountries = apiService.getAllCountries();
                Platform.runLater(() -> {
                    displayCountries(allCountries);
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showError("Failed to load countries: " + e.getMessage());
                });
            }
        }).start();
    }
    
    private void displayCountries(List<Country> countries) {
        countriesFlowPane.getChildren().clear();
        
        for (Country country : countries) {
            VBox card = createCountryCard(country);
            countriesFlowPane.getChildren().add(card);
        }
    }
    
    private VBox createCountryCard(Country country) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); -fx-pref-width: 300;");
        
        Label nameLabel = new Label(country.getFlag() + " " + country.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label codeLabel = new Label("Code: " + country.getCode());
        codeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        if (country.getSummary() != null && !country.getSummary().isEmpty()) {
            Label summaryLabel = new Label(country.getSummary());
            summaryLabel.setWrapText(true);
            summaryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c3e50;");
            card.getChildren().add(summaryLabel);
        }
        
        Button viewButton = new Button("View Details");
        viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");
        viewButton.setOnAction(e -> navigateToCountryDetail(country.getCode()));
        
        card.getChildren().addAll(nameLabel, codeLabel, viewButton);
        
        return card;
    }
    
    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            displayCountries(allCountries);
            return;
        }
        
        List<Country> filtered = allCountries.stream()
            .filter(c -> c.getName().toLowerCase().contains(query) || 
                        c.getCode().toLowerCase().contains(query))
            .collect(Collectors.toList());
        
        displayCountries(filtered);
    }
    
    @FXML
    private void handleFilter() {
        // Filter by region if needed (would require region data in Country model)
        handleSearch(); // Reapply search with current filter
    }
    
    private void navigateToCountryDetail(String countryCode) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/CountryDetail.fxml"));
            javafx.scene.Parent root = loader.load();
            CountryDetailController controller = loader.getController();
            controller.loadCountry(countryCode);
            javafx.stage.Stage stage = (javafx.stage.Stage) countriesFlowPane.getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load country details: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        // Silent error logging - no popup
        System.err.println("[CountriesController] " + message);
    }
    
    // Navigation methods
    @FXML private void navigateToDashboard(javafx.event.ActionEvent event) { NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/Dashboard.fxml"); }
    @FXML private void navigateToCountries(javafx.event.ActionEvent event) { /* Already on countries */ }
    @FXML private void navigateToNews(javafx.event.ActionEvent event) { NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/News.fxml"); }
    @FXML private void navigateToProfile(javafx.event.ActionEvent event) { NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/Profile.fxml"); }
    @FXML private void navigateToAbout(javafx.event.ActionEvent event) { NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/About.fxml"); }
    @FXML private void handleLogout(javafx.event.ActionEvent event) {
        apiService.logout();
        NavigationHelper.navigate((javafx.scene.Node) event.getSource(), "/fxml/Login.fxml");
    }
}
