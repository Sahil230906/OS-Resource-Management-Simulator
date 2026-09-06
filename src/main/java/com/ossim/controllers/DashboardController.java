package com.ossim.controllers;

import com.ossim.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DashboardController {

    @FXML private Button cpuButton;
    @FXML private Button comparisonButton;
    @FXML private Button memoryButton;
    @FXML private Button pageButton;
    @FXML private Button diskButton;

    @FXML
    public void initialize() {
        // Sidebar navigation wiring — Deadlock will be
        // connected here as it's built in the next milestone.
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

        System.out.println("Dashboard loaded successfully.");
    }
}