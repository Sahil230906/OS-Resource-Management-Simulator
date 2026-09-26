package com.ossim.controllers;

import com.ossim.Main;
import com.ossim.services.DemoModeState;
import com.ossim.visualization.AnimationUtil;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class DashboardController {

    @FXML private Button cpuButton;
    @FXML private Button comparisonButton;
    @FXML private Button memoryButton;
    @FXML private Button pageButton;
    @FXML private Button diskButton;
    @FXML private Button deadlockButton;
    @FXML private Button runEverythingButton;

    @FXML private FlowPane cardContainer;
    @FXML private VBox cpuCard;
    @FXML private VBox memoryCard;
    @FXML private VBox pageCard;
    @FXML private VBox diskCard;
    @FXML private VBox deadlockCard;
    @FXML private VBox comparisonCard;

    @FXML
    public void initialize() {

        // ===== Sidebar buttons =====
        if (cpuButton != null) {
            cpuButton.setOnAction(e -> Main.switchScreen("/fxml/CpuScheduling.fxml"));
        }

        if (comparisonButton != null) {
            comparisonButton.setOnAction(e -> Main.switchScreen("/fxml/Comparison.fxml"));
        }

        if (memoryButton != null) {
            memoryButton.setOnAction(e -> Main.switchScreen("/fxml/MemoryManagement.fxml"));
        }

        if (pageButton != null) {
            pageButton.setOnAction(e -> Main.switchScreen("/fxml/PageReplacement.fxml"));
        }

        if (diskButton != null) {
            diskButton.setOnAction(e -> Main.switchScreen("/fxml/DiskScheduling.fxml"));
        }

        if (deadlockButton != null) {
            deadlockButton.setOnAction(e -> Main.switchScreen("/fxml/DeadlockDetection.fxml"));
        }

        // ===== Run Everything (demo mode) — acts as a toggle =====
        if (runEverythingButton != null) {
            updateRunEverythingButtonLabel();

            runEverythingButton.setOnAction(e -> {
                if (DemoModeState.isActive()) {
                    // Currently touring — this click ends it
                    DemoModeState.setActive(false);
                    updateRunEverythingButtonLabel();
                } else {
                    // Not touring — this click starts it and jumps to the first module
                    DemoModeState.setActive(true);
                    Main.switchScreen("/fxml/CpuScheduling.fxml");
                }
            });
        }

        // ===== Dashboard cards (same destinations as the sidebar, second entry point) =====
        if (cpuCard != null) {
            cpuCard.setOnMouseClicked(e -> Main.switchScreen("/fxml/CpuScheduling.fxml"));
        }

        if (memoryCard != null) {
            memoryCard.setOnMouseClicked(e -> Main.switchScreen("/fxml/MemoryManagement.fxml"));
        }

        if (pageCard != null) {
            pageCard.setOnMouseClicked(e -> Main.switchScreen("/fxml/PageReplacement.fxml"));
        }

        if (diskCard != null) {
            diskCard.setOnMouseClicked(e -> Main.switchScreen("/fxml/DiskScheduling.fxml"));
        }

        if (deadlockCard != null) {
            deadlockCard.setOnMouseClicked(e -> Main.switchScreen("/fxml/DeadlockDetection.fxml"));
        }

        if (comparisonCard != null) {
            comparisonCard.setOnMouseClicked(e -> Main.switchScreen("/fxml/Comparison.fxml"));
        }

        if (cardContainer != null) {
            revealAllTogether(cardContainer.getChildren());
        }

        System.out.println("Dashboard loaded successfully.");
    }

    /**
     * Fades and scales every dashboard card in at the same time, rather than
     * one after another. Used only here — the module visualizers (Gantt chart,
     * memory map, etc.) still use AnimationUtil.revealSequentially since those
     * are meant to play out as a sequence of events.
     */
    private void revealAllTogether(Iterable<Node> nodes) {
        ParallelTransition allCards = new ParallelTransition();

        for (Node card : nodes) {
            card.setOpacity(0);
            card.setScaleX(0.85);
            card.setScaleY(0.85);

            FadeTransition fade = new FadeTransition(Duration.millis(350), card);
            fade.setToValue(1);

            ScaleTransition scale = new ScaleTransition(Duration.millis(350), card);
            scale.setToX(1);
            scale.setToY(1);

            allCards.getChildren().add(new ParallelTransition(card, fade, scale));
        }

        allCards.play();
    }

    private void updateRunEverythingButtonLabel() {
        if (DemoModeState.isActive()) {
            runEverythingButton.setText("⏹ Exit Demo Mode");
        } else {
            runEverythingButton.setText("▶ Run Everything (Demo Mode)");
        }
    }
}