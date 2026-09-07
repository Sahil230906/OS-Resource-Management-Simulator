package com.ossim.controllers;

import com.ossim.Main;
import com.ossim.visualization.AnimationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class DashboardController {

    @FXML private Button cpuButton;
    @FXML private Button comparisonButton;
    @FXML private Button memoryButton;
    @FXML private Button pageButton;
    @FXML private Button diskButton;
    @FXML private Button deadlockButton;

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

        // Cards pop in one after another on load, purely cosmetic —
        // the cards are already fully built and clickable regardless
        if (cardContainer != null) {
            AnimationUtil.revealSequentially(cardContainer.getChildren());
        }

        System.out.println("Dashboard loaded successfully.");
    }
}