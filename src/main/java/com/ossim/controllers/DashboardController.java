package com.ossim.controllers;

import com.ossim.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DashboardController {

    @FXML private Button cpuButton;
    @FXML private Button comparisonButton;

    @FXML
    public void initialize() {
        // Sidebar navigation wiring — other modules (Memory, Paging, Disk,
        // Deadlock) will be connected here as they're built in later milestones.
        if (cpuButton != null) {
            cpuButton.setOnAction(e -> Main.switchScreen("/fxml/CpuScheduling.fxml"));
        }

        if (comparisonButton != null) {
            comparisonButton.setOnAction(e -> Main.switchScreen("/fxml/Comparison.fxml"));
        }

        System.out.println("Dashboard loaded successfully.");
    }
}