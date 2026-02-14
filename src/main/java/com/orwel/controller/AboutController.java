package com.orwel.controller;

import com.orwel.service.ApiService;
import com.orwel.util.NavigationHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;

public class AboutController {
    private ApiService apiService = ApiService.getInstance();
    
    // Navigation methods
    @FXML private void navigateToDashboard(ActionEvent event) { 
        NavigationHelper.navigate((Node) event.getSource(), "/fxml/Dashboard.fxml"); 
    }
    @FXML private void navigateToCountries(ActionEvent event) { 
        NavigationHelper.navigate((Node) event.getSource(), "/fxml/Countries.fxml"); 
    }
    @FXML private void navigateToNews(ActionEvent event) { 
        NavigationHelper.navigate((Node) event.getSource(), "/fxml/News.fxml"); 
    }
    @FXML private void navigateToProfile(ActionEvent event) { 
        NavigationHelper.navigate((Node) event.getSource(), "/fxml/Profile.fxml"); 
    }
    @FXML private void navigateToAbout(ActionEvent event) { /* Already on about */ }
    @FXML private void handleLogout(ActionEvent event) {
        apiService.logout();
        NavigationHelper.navigate((Node) event.getSource(), "/fxml/Login.fxml");
    }
}
