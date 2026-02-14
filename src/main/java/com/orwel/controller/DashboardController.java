package com.orwel.controller;

import com.orwel.config.AppConfig;
import com.orwel.model.Country;
import com.orwel.model.NewsArticle;
import com.orwel.model.User;
import com.orwel.model.Warning;
import com.orwel.service.ApiService;
import com.orwel.util.AnimationUtils;
import com.orwel.util.NavigationHelper;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label welcomeDescriptionLabel;
    @FXML private Label countriesCountLabel;
    @FXML private Label warningsCountLabel;
    @FXML private Label newsCountLabel;
    @FXML private VBox countriesCard;
    @FXML private VBox warningsCard;
    @FXML private VBox newsCard;
    @FXML private VBox welcomeCard;
    @FXML private VBox warningsCardSection;
    @FXML private VBox countriesCardSection;
    @FXML private VBox newsCardSection;
    @FXML private VBox dashboardContent;
    @FXML private ListView<Warning> warningsListView;
    @FXML private ListView<Country> countriesListView;
    @FXML private ListView<NewsArticle> newsListView;
    
    private ApiService apiService = ApiService.getInstance();
    
    @FXML
    public void initialize() {
        // Set welcome description from config
        if (welcomeDescriptionLabel != null) {
            welcomeDescriptionLabel.setText(AppConfig.APP_WELCOME_DESCRIPTION);
        }
        
        loadDashboardData();
        setupListViews();
        addAnimations();
    }
    
    private void addAnimations() {
        Platform.runLater(() -> {
            // Staggered entrance animations for all cards
            AnimationUtils.staggerFadeInSlideUp(
                Arrays.asList(welcomeCard, countriesCard, warningsCard, newsCard, 
                             warningsCardSection, countriesCardSection, newsCardSection),
                100
            );
            
            // Add hover effects to stat cards
            AnimationUtils.addHoverScaleEffect(countriesCard);
            AnimationUtils.addHoverScaleEffect(warningsCard);
            AnimationUtils.addHoverScaleEffect(newsCard);
            
            // Add neon glow to buttons
            AnimationUtils.addNeonGlowEffect(welcomeLabel);
        });
    }
    
    private void setupListViews() {
        // Setup warnings list view
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
                            messageLabel.setStyle("-fx-font-size: 12px;");
                            vbox.getChildren().addAll(titleLabel, messageLabel);
                            setGraphic(vbox);
                        }
                    }
                };
            }
        });
        
        // Setup countries list view
        countriesListView.setCellFactory(new Callback<ListView<Country>, ListCell<Country>>() {
            @Override
            public ListCell<Country> call(ListView<Country> param) {
                return new ListCell<Country>() {
                    @Override
                    protected void updateItem(Country country, boolean empty) {
                        super.updateItem(country, empty);
                        if (empty || country == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(country.getFlag() + " " + country.getName());
                            setOnMouseClicked(e -> {
                                if (e.getClickCount() == 2) {
                                    navigateToCountryDetail(country.getCode());
                                }
                            });
                        }
                    }
                };
            }
        });
        
        // Setup news list view
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
    
    private void loadDashboardData() {
        // Load user info
        new Thread(() -> {
            try {
                User user = apiService.getCurrentUser();
                if (user != null && user.getFirstName() != null) {
                    Platform.runLater(() -> {
                        if (welcomeLabel != null) {
                            welcomeLabel.setText("Welcome, " + user.getFirstName() + "!");
                        }
                    });
                } else {
                    Platform.runLater(() -> {
                        if (welcomeLabel != null) {
                            welcomeLabel.setText("Welcome!");
                        }
                    });
                }
            } catch (IOException e) {
                // Backend not available - use demo data
                Platform.runLater(() -> {
                    if (welcomeLabel != null) {
                        welcomeLabel.setText("Welcome!");
                    }
                    if (countriesCountLabel != null) {
                        countriesCountLabel.setText("50");
                    }
                    if (warningsCountLabel != null) {
                        warningsCountLabel.setText("5");
                    }
                    if (newsCountLabel != null) {
                        newsCountLabel.setText("20");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        // Load countries
        new Thread(() -> {
            try {
                List<Country> countries = apiService.getAllCountries();
                if (countries != null && !countries.isEmpty()) {
                    Platform.runLater(() -> {
                        if (countriesCountLabel != null) {
                            countriesCountLabel.setText(String.valueOf(countries.size()));
                        }
                        if (countriesListView != null) {
                            countriesListView.getItems().setAll(countries.subList(0, Math.min(5, countries.size())));
                        }
                    });
                }
            } catch (IOException e) {
                // Backend not available - skip
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        // Load personalized news
        new Thread(() -> {
            try {
                List<NewsArticle> news = apiService.getPersonalizedNews();
                if (news != null && !news.isEmpty()) {
                    Platform.runLater(() -> {
                        if (newsCountLabel != null) {
                            newsCountLabel.setText(String.valueOf(news.size()));
                        }
                        if (newsListView != null) {
                            newsListView.getItems().setAll(news.subList(0, Math.min(5, news.size())));
                        }
                    });
                }
            } catch (IOException e) {
                // Backend not available - skip
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
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
    
    @FXML
    private void viewAllWarnings() {
        // Navigate to warnings page or show all warnings
        System.out.println("[DashboardController] View all warnings feature - can be implemented as a separate page");
    }
    
    private void navigateToCountryDetail(String countryCode) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/CountryDetail.fxml"));
            javafx.scene.Parent root = loader.load();
            CountryDetailController controller = loader.getController();
            controller.loadCountry(countryCode);
            javafx.stage.Stage stage = (javafx.stage.Stage) welcomeLabel.getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[DashboardController] Failed to load country details: " + e.getMessage());
        }
    }
}
