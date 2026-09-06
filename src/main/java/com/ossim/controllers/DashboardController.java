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
    @FXML private Button deadlockButton;

    @FXML
    public void initialize() {
        // All core modules are now wired. M9 covers integration, polish,
        // and extending Comparison to Memory/Paging/Disk.
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

        System.out.println("Dashboard loaded successfully.");
    }
}