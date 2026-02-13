package com.orwel.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationHelper {
    /**
     * Navigate to a new FXML page with proper CSS styling and error handling
     * @param sourceNode The node from the current scene (used to get the stage)
     * @param fxmlPath The path to the FXML file (e.g., "/fxml/Dashboard.fxml")
     */
    public static void navigate(Node sourceNode, String fxmlPath) {
        if (sourceNode == null || sourceNode.getScene() == null) {
            System.err.println("Invalid source node for navigation");
            return;
        }
        
        try {
            java.net.URL resource = NavigationHelper.class.getResource(fxmlPath);
            if (resource == null) {
                throw new IOException("Resource not found: " + fxmlPath);
            }
            
            Parent root = FXMLLoader.load(resource);
            if (root == null) {
                throw new IOException("Failed to load FXML root");
            }
            
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            if (stage == null) {
                throw new IOException("Stage not found");
            }
            
            Scene scene = new Scene(root);
            
            // Always apply CSS stylesheet
            java.net.URL cssResource = NavigationHelper.class.getResource("/styles.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }
            
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlPath);
            e.printStackTrace();
            // Show error alert on JavaFX thread
            javafx.application.Platform.runLater(() -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Navigation Error");
                alert.setHeaderText("Failed to load page");
                alert.setContentText("Could not load: " + fxmlPath + "\n" + e.getMessage());
                alert.showAndWait();
            });
        } catch (Exception e) {
            System.err.println("Unexpected error during navigation: " + fxmlPath);
            e.printStackTrace();
        }
    }
    
    /**
     * Navigate with fade transition animation
     */
    public static void navigateWithTransition(Node sourceNode, String fxmlPath) {
        try {
            // Fade out current scene
            Parent currentRoot = sourceNode.getScene().getRoot();
            javafx.animation.FadeTransition fadeOut = AnimationUtils.pageTransition(currentRoot, false);
            fadeOut.setOnFinished(e -> {
                try {
                    Parent root = FXMLLoader.load(NavigationHelper.class.getResource(fxmlPath));
                    root.setOpacity(0);
                    Stage stage = (Stage) sourceNode.getScene().getWindow();
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(NavigationHelper.class.getResource("/styles.css").toExternalForm());
                    stage.setScene(scene);
                    
                    // Fade in new scene
                    AnimationUtils.pageTransition(root, true).play();
                } catch (IOException ex) {
                    System.err.println("Failed to load FXML: " + fxmlPath);
                    ex.printStackTrace();
                }
            });
            fadeOut.play();
        } catch (Exception e) {
            // Fallback to simple navigation
            navigate(sourceNode, fxmlPath);
        }
    }
}
