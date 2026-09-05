package com.ossim.controllers;

import com.ossim.Main;
import com.ossim.algorithms.cpu.*;
import com.ossim.models.CpuSchedulingResult;
import com.ossim.models.Process;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

import java.util.ArrayList;
import java.util.List;

public class ComparisonController {

    @FXML private Button backButton;

    @FXML private TableView<Process> processTable;
    @FXML private TableColumn<Process, String> colProcessId;
    @FXML private TableColumn<Process, Integer> colArrivalTime;
    @FXML private TableColumn<Process, Integer> colBurstTime;
    @FXML private TableColumn<Process, Integer> colPriority;
    @FXML private Button addProcessButton;
    @FXML private Button removeProcessButton;
    @FXML private Button clearButton;
    @FXML private Button loadExampleButton;

    @FXML private CheckBox fcfsCheck;
    @FXML private CheckBox sjfCheck;
    @FXML private CheckBox priorityCheck;
    @FXML private CheckBox rrCheck;
    @FXML private TextField quantumField;
    @FXML private Button compareButton;

    @FXML private VBox resultsSection;
    @FXML private TableView<ComparisonRow> comparisonTable;
    @FXML private TableColumn<ComparisonRow, String> colAlgoName;
    @FXML private TableColumn<ComparisonRow, String> colAvgWaiting;
    @FXML private TableColumn<ComparisonRow, String> colAvgTurnaround;
    @FXML private TableColumn<ComparisonRow, String> colAvgResponse;

    @FXML private VBox winnerSection;
    @FXML private Label winnerNameLabel;
    @FXML private Label winnerReasonLabel;

    private final ObservableList<Process> processList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (backButton != null) {
            backButton.setOnAction(e -> Main.switchScreen("/fxml/Dashboard.fxml"));
        }

        setupProcessTable();
        setupComparisonTable();

        addProcessButton.setOnAction(e -> onAddProcess());
        removeProcessButton.setOnAction(e -> onRemoveSelectedProcess());
        clearButton.setOnAction(e -> processList.clear());
        loadExampleButton.setOnAction(e -> onLoadExample());
        compareButton.setOnAction(e -> onCompare());
    }

    private void setupProcessTable() {
        colProcessId.setCellValueFactory(new PropertyValueFactory<>("processId"));
        colArrivalTime.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        colBurstTime.setCellValueFactory(new PropertyValueFactory<>("burstTime"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));

        colProcessId.setCellFactory(TextFieldTableCell.forTableColumn());
        colArrivalTime.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colBurstTime.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colPriority.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        colProcessId.setOnEditCommit(e -> e.getRowValue().setProcessId(e.getNewValue()));
        colArrivalTime.setOnEditCommit(e -> e.getRowValue().setArrivalTime(e.getNewValue()));
        colBurstTime.setOnEditCommit(e -> e.getRowValue().setBurstTime(e.getNewValue()));
        colPriority.setOnEditCommit(e -> e.getRowValue().setPriority(e.getNewValue()));

        processTable.setItems(processList);
        processTable.setEditable(true);
    }

    private void setupComparisonTable() {
        colAlgoName.setCellValueFactory(new PropertyValueFactory<>("algorithmName"));
        colAvgWaiting.setCellValueFactory(new PropertyValueFactory<>("avgWaiting"));
        colAvgTurnaround.setCellValueFactory(new PropertyValueFactory<>("avgTurnaround"));
        colAvgResponse.setCellValueFactory(new PropertyValueFactory<>("avgResponse"));
    }

    private void onAddProcess() {
        int nextNumber = processList.size() + 1;
        processList.add(new Process("P" + nextNumber, 0, 1, 1));
    }

    private void onRemoveSelectedProcess() {
        Process selected = processTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            processList.remove(selected);
        }
    }

    private void onLoadExample() {
        processList.clear();
        processList.addAll(
                new Process("P1", 0, 5, 2),
                new Process("P2", 1, 3, 1),
                new Process("P3", 2, 7, 3)
        );
    }

    private void onCompare() {

        if (processList.isEmpty()) {
            showError("Please add at least one process before comparing.");
            return;
        }

        for (Process p : processList) {
            if (p.getBurstTime() <= 0) {
                showError("Burst time must be greater than zero for process " + p.getProcessId());
                return;
            }
            if (p.getArrivalTime() < 0) {
                showError("Arrival time cannot be negative for process " + p.getProcessId());
                return;
            }
        }

        List<CpuSchedulingAlgorithm> selectedAlgorithms = new ArrayList<>();

        if (fcfsCheck.isSelected()) selectedAlgorithms.add(new FCFS());
        if (sjfCheck.isSelected()) selectedAlgorithms.add(new SJF());
        if (priorityCheck.isSelected()) selectedAlgorithms.add(new PriorityScheduling());

        if (rrCheck.isSelected()) {
            int quantum;
            try {
                quantum = Integer.parseInt(quantumField.getText().trim());
                if (quantum <= 0) {
                    showError("Time quantum must be greater than zero.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showError("Time quantum must be a valid whole number.");
                return;
            }
            selectedAlgorithms.add(new RoundRobin(quantum));
        }

        if (selectedAlgorithms.isEmpty()) {
            showError("Please select at least one algorithm to compare.");
            return;
        }

        List<ComparisonRow> rows = new ArrayList<>();
        String bestAlgorithmName = null;
        double bestAvgWaiting = Double.MAX_VALUE;

        for (CpuSchedulingAlgorithm algorithm : selectedAlgorithms) {
            CpuSchedulingResult result = algorithm.run(processList);

            rows.add(new ComparisonRow(
                    algorithm.getName(),
                    String.format("%.2f", result.getAverageWaitingTime()),
                    String.format("%.2f", result.getAverageTurnaroundTime()),
                    String.format("%.2f", result.getAverageResponseTime())
            ));

            if (result.getAverageWaitingTime() < bestAvgWaiting) {
                bestAvgWaiting = result.getAverageWaitingTime();
                bestAlgorithmName = algorithm.getName();
            }
        }

        comparisonTable.setItems(FXCollections.observableArrayList(rows));
        resultsSection.setVisible(true);
        resultsSection.setManaged(true);

        winnerNameLabel.setText(bestAlgorithmName);
        winnerReasonLabel.setText(String.format("Lowest Average Waiting Time (%.2f ms)", bestAvgWaiting));
        winnerSection.setVisible(true);
        winnerSection.setManaged(true);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class ComparisonRow {
        private final String algorithmName;
        private final String avgWaiting;
        private final String avgTurnaround;
        private final String avgResponse;

        public ComparisonRow(String algorithmName, String avgWaiting, String avgTurnaround, String avgResponse) {
            this.algorithmName = algorithmName;
            this.avgWaiting = avgWaiting;
            this.avgTurnaround = avgTurnaround;
            this.avgResponse = avgResponse;
        }

        public String getAlgorithmName() { return algorithmName; }
        public String getAvgWaiting() { return avgWaiting; }
        public String getAvgTurnaround() { return avgTurnaround; }
        public String getAvgResponse() { return avgResponse; }
    }
}