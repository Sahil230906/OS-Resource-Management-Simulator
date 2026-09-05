package com.ossim.controllers;

import com.ossim.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DashboardController {

    @FXML private Button cpuButton;

    @FXML
    public void initialize() {
        // Only CPU Scheduling is wired for now — other modules will be
        // connected here as they're built in later milestones (Memory,
        // Paging, Disk, Deadlock, Comparison).
        if (cpuButton != null) {
            cpuButton.setOnAction(e -> Main.switchScreen("/fxml/CpuScheduling.fxml"));
        }

        System.out.println("Dashboard loaded successfully.");
    }
}