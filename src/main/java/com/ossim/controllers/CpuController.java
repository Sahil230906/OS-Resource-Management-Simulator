package com.ossim.controllers;

import com.ossim.Main;
import com.ossim.algorithms.cpu.*;
import com.ossim.models.CpuSchedulingResult;
import com.ossim.models.Process;
import com.ossim.models.ProcessResult;
import com.ossim.visualization.GanttChartRenderer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

import java.util.HashMap;
import java.util.Map;

public class CpuController {

    // ===== Input table =====
    @FXML private TableView<Process> processTable;
    @FXML private TableColumn<Process, String> colProcessId;
    @FXML private TableColumn<Process, Integer> colArrivalTime;
    @FXML private TableColumn<Process, Integer> colBurstTime;
    @FXML private TableColumn<Process, Integer> colPriority;

    @FXML private Button addProcessButton;
    @FXML private Button removeProcessButton;
    @FXML private Button clearButton;
    @FXML private Button loadExampleButton;
    @FXML private Button backButton;

    // ===== Algorithm selection =====
    @FXML private ComboBox<String> algorithmSelector;
    @FXML private Label quantumLabel;
    @FXML private TextField quantumField;
    @FXML private Button runButton;

    // ===== Metrics =====
    @FXML private VBox metricsSection;
    @FXML private Label avgWaitingLabel;
    @FXML private Label avgTurnaroundLabel;
    @FXML private Label avgResponseLabel;

    // ===== Gantt chart =====
    @FXML private VBox ganttSection;
    @FXML private VBox ganttContainer;

    // ===== Results table =====
    @FXML private VBox resultsSection;
    @FXML private TableView<ProcessResult> resultsTable;
    @FXML private TableColumn<ProcessResult, String> colResultId;
    @FXML private TableColumn<ProcessResult, Integer> colResultArrival;
    @FXML private TableColumn<ProcessResult, Integer> colResultBurst;
    @FXML private TableColumn<ProcessResult, Integer> colResultCompletion;
    @FXML private TableColumn<ProcessResult, Integer> colResultWaiting;
    @FXML private TableColumn<ProcessResult, Integer> colResultTurnaround;
    @FXML private TableColumn<ProcessResult, Integer> colResultResponse;

    // ===== Explanation =====
    @FXML private Label explanationLabel;

    private final ObservableList<Process> processList = FXCollections.observableArrayList();
    private final Map<String, String> explanations = new HashMap<>();

    @FXML
    public void initialize() {
        setupProcessTable();
        setupExplanations();
        setupAlgorithmSelector();
        setupResultsTable();

        addProcessButton.setOnAction(e -> onAddProcess());
        removeProcessButton.setOnAction(e -> onRemoveSelectedProcess());
        clearButton.setOnAction(e -> processList.clear());
        loadExampleButton.setOnAction(e -> onLoadExample());
        runButton.setOnAction(e -> onRunSimulation());

        if (backButton != null) {
            backButton.setOnAction(e -> Main.switchScreen("/fxml/Dashboard.fxml"));
        }

        // Time Quantum field only matters for Round Robin — hidden otherwise
        quantumLabel.setVisible(false);
        quantumLabel.setManaged(false);
        quantumField.setVisible(false);
        quantumField.setManaged(false);
    }

    private void setupProcessTable() {
        colProcessId.setCellValueFactory(new PropertyValueFactory<>("processId"));
        colArrivalTime.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        colBurstTime.setCellValueFactory(new PropertyValueFactory<>("burstTime"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));

        // Make cells editable via double-click
        colProcessId.setCellFactory(TextFieldTableCell.forTableColumn());
        colArrivalTime.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colBurstTime.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colPriority.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        // Commit edited values back into the Process object
        colProcessId.setOnEditCommit(e -> e.getRowValue().setProcessId(e.getNewValue()));
        colArrivalTime.setOnEditCommit(e -> e.getRowValue().setArrivalTime(e.getNewValue()));
        colBurstTime.setOnEditCommit(e -> e.getRowValue().setBurstTime(e.getNewValue()));
        colPriority.setOnEditCommit(e -> e.getRowValue().setPriority(e.getNewValue()));

        processTable.setItems(processList);
        processTable.setEditable(true);
    }

    private void setupAlgorithmSelector() {
        algorithmSelector.setItems(FXCollections.observableArrayList(
                "FCFS", "SJF", "Priority Scheduling", "Round Robin"
        ));
        algorithmSelector.setValue("FCFS");

        algorithmSelector.setOnAction(e -> {
            boolean isRoundRobin = "Round Robin".equals(algorithmSelector.getValue());
            quantumLabel.setVisible(isRoundRobin);
            quantumLabel.setManaged(isRoundRobin);
            quantumField.setVisible(isRoundRobin);
            quantumField.setManaged(isRoundRobin);

            explanationLabel.setText(explanations.getOrDefault(
                    algorithmSelector.getValue(), "Select an algorithm to see its explanation."
            ));
        });

        explanationLabel.setText(explanations.getOrDefault("FCFS", ""));
    }

    private void setupResultsTable() {
        colResultId.setCellValueFactory(new PropertyValueFactory<>("processId"));
        colResultArrival.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        colResultBurst.setCellValueFactory(new PropertyValueFactory<>("burstTime"));
        colResultCompletion.setCellValueFactory(new PropertyValueFactory<>("completionTime"));
        colResultWaiting.setCellValueFactory(new PropertyValueFactory<>("waitingTime"));
        colResultTurnaround.setCellValueFactory(new PropertyValueFactory<>("turnaroundTime"));
        colResultResponse.setCellValueFactory(new PropertyValueFactory<>("responseTime"));
    }

    private void setupExplanations() {
        explanations.put("FCFS",
                "First Come First Serve executes processes strictly in the order they arrive. " +
                "Simple to implement, but a long process can delay all processes behind it (convoy effect).");
        explanations.put("SJF",
                "Shortest Job First always picks the process with the smallest burst time among those " +
                "that have arrived. Minimizes average waiting time, but can starve longer processes.");
        explanations.put("Priority Scheduling",
                "Each process is assigned a priority; the process with the highest priority (lowest number) " +
                "among arrived processes runs next. Low-priority processes may starve indefinitely.");
        explanations.put("Round Robin",
                "Each process receives a fixed time quantum. If it doesn't finish within that quantum, " +
                "it is placed at the back of the ready queue. Fair and responsive, but overhead increases " +
                "if the quantum is too small.");
    }

    // ===== Button Actions =====

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

    private void onRunSimulation() {

        if (processList.isEmpty()) {
            showError("Please add at least one process before running the simulation.");
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

        String selectedAlgorithm = algorithmSelector.getValue();
        CpuSchedulingAlgorithm algorithm = createAlgorithm(selectedAlgorithm);

        if (algorithm == null) {
            return; // error already shown inside createAlgorithm()
        }

        CpuSchedulingResult result = algorithm.run(processList);
        displayResults(result);
    }

    private CpuSchedulingAlgorithm createAlgorithm(String name) {
        switch (name) {
            case "FCFS":
                return new FCFS();
            case "SJF":
                return new SJF();
            case "Priority Scheduling":
                return new PriorityScheduling();
            case "Round Robin":
                int quantum;
                try {
                    quantum = Integer.parseInt(quantumField.getText().trim());
                    if (quantum <= 0) {
                        showError("Time quantum must be greater than zero.");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    showError("Time quantum must be a valid whole number.");
                    return null;
                }
                return new RoundRobin(quantum);
            default:
                return null;
        }
    }

    private void displayResults(CpuSchedulingResult result) {

        avgWaitingLabel.setText(String.format("%.2f ms", result.getAverageWaitingTime()));
        avgTurnaroundLabel.setText(String.format("%.2f ms", result.getAverageTurnaroundTime()));
        avgResponseLabel.setText(String.format("%.2f ms", result.getAverageResponseTime()));
        metricsSection.setVisible(true);
        metricsSection.setManaged(true);

        ganttContainer.getChildren().clear();
        ganttContainer.getChildren().add(GanttChartRenderer.render(result.getGanttEntries()));
        ganttSection.setVisible(true);
        ganttSection.setManaged(true);

        resultsTable.setItems(FXCollections.observableArrayList(result.getProcessResults()));
        resultsSection.setVisible(true);
        resultsSection.setManaged(true);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}