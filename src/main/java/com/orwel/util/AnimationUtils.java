package com.orwel.util;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.List;

/**
 * Utility class for creating smooth, staggered animations inspired by modern web design
 */
public class AnimationUtils {
    
    /**
     * Creates a fade-in and slide-up entrance animation
     * @param node The node to animate
     * @param delayMs Delay in milliseconds before animation starts
     * @return The animation timeline
     */
    public static ParallelTransition fadeInSlideUp(Node node, long delayMs) {
        // Set initial state
        node.setOpacity(0);
        node.setTranslateY(30);
        
        // Create fade transition
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(600), node);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        
        // Create slide transition
        TranslateTransition slideTransition = new TranslateTransition(Duration.millis(600), node);
        slideTransition.setFromY(30);
        slideTransition.setToY(0);
        
        // Combine both animations
        ParallelTransition parallelTransition = new ParallelTransition(fadeTransition, slideTransition);
        parallelTransition.setDelay(Duration.millis(delayMs));
        
        return parallelTransition;
    }
    
    /**
     * Creates a staggered animation for a list of nodes
     * @param nodes List of nodes to animate
     * @param delayBetweenMs Delay between each node's animation start
     */
    public static void staggerFadeInSlideUp(List<Node> nodes, long delayBetweenMs) {
        for (int i = 0; i < nodes.size(); i++) {
            fadeInSlideUp(nodes.get(i), i * delayBetweenMs).play();
        }
    }
    
    /**
     * Adds hover scale effect to a node (scale to 1.05x on hover)
     * @param node The node to add hover effect to
     */
    public static void addHoverScaleEffect(Node node) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), node);
        
        node.setOnMouseEntered(e -> {
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);
            scaleTransition.play();
        });
        
        node.setOnMouseExited(e -> {
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });
    }
    
    /**
     * Adds neon glow effect on hover
     * @param node The node to add glow effect to
     */
    public static void addNeonGlowEffect(Node node) {
        javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow();
        glow.setColor(javafx.scene.paint.Color.rgb(0, 242, 255, 0.5));
        glow.setRadius(15);
        glow.setSpread(0.5);
        
        javafx.scene.effect.DropShadow glowStrong = new javafx.scene.effect.DropShadow();
        glowStrong.setColor(javafx.scene.paint.Color.rgb(0, 242, 255, 0.8));
        glowStrong.setRadius(25);
        glowStrong.setSpread(0.7);
        
        node.setOnMouseEntered(e -> node.setEffect(glowStrong));
        node.setOnMouseExited(e -> node.setEffect(glow));
    }
    
    /**
     * Creates a pulsing glow animation
     * @param node The node to animate
     * @return The animation timeline
     */
    public static Timeline createPulseGlow(Node node) {
        javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow();
        glow.setColor(javafx.scene.paint.Color.rgb(0, 242, 255, 0.3));
        glow.setRadius(15);
        
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, e -> {
                glow.setRadius(15);
                glow.setColor(javafx.scene.paint.Color.rgb(0, 242, 255, 0.3));
                node.setEffect(glow);
            }),
            new KeyFrame(Duration.millis(1000), e -> {
                glow.setRadius(25);
                glow.setColor(javafx.scene.paint.Color.rgb(0, 242, 255, 0.6));
                node.setEffect(glow);
            }),
            new KeyFrame(Duration.millis(2000), e -> {
                glow.setRadius(15);
                glow.setColor(javafx.scene.paint.Color.rgb(0, 242, 255, 0.3));
                node.setEffect(glow);
            })
        );
        
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        
        return timeline;
    }
    
    /**
     * Creates a smooth page transition fade effect
     * @param node The node to fade
     * @param fadeIn True for fade in, false for fade out
     * @return The fade transition
     */
    public static FadeTransition pageTransition(Node node, boolean fadeIn) {
        FadeTransition fade = new FadeTransition(Duration.millis(400), node);
        if (fadeIn) {
            fade.setFromValue(0);
            fade.setToValue(1);
        } else {
            fade.setFromValue(1);
            fade.setToValue(0);
        }
        return fade;
    }
    
    /**
     * Adds a subtle float animation (up and down)
     * @param node The node to animate
     * @return The animation transition
     */
    public static TranslateTransition createFloatAnimation(Node node) {
        TranslateTransition floatTransition = new TranslateTransition(Duration.millis(3000), node);
        floatTransition.setFromY(0);
        floatTransition.setToY(-10);
        floatTransition.setAutoReverse(true);
        floatTransition.setCycleCount(TranslateTransition.INDEFINITE);
        
        return floatTransition;
    }
}
