package com.orwel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.orwel.config.AppConfig;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load configuration
        AppConfig.loadConfig();
        
        // Load login page as entry point
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        Scene scene = new Scene(root, 1400, 900);
        
        // Apply CSS
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        primaryStage.setTitle("Orwel - Government Policy Tracker");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);
        
        // Center window
        primaryStage.centerOnScreen();
        
        // Add fade in animation
        primaryStage.setOpacity(0);
        primaryStage.show();
        
        javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.millis(500), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setOnFinished(e -> primaryStage.setOpacity(1));
        fadeIn.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
