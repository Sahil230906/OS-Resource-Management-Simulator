package com.ossim.controllers;

import com.ossim.Main;
import com.ossim.algorithms.disk.*;
import com.ossim.models.DiskRequest;
import com.ossim.models.DiskSchedulingResult;
import com.ossim.models.DiskStepResult;
import com.ossim.visualization.DiskVisualizer;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DiskController {

    // ===== Track request input =====
    @FXML private TableView<DiskRequest> requestTable;
    @FXML private TableColumn<DiskRequest, Integer> colTrackNumber;

    @FXML private Button addRequestButton;
    @FXML private Button removeRequestButton;
    @FXML private Button clearRequestButton;
    @FXML private Button loadRequestExampleButton;
    @FXML private Button backButton;

    // ===== Disk settings + algorithm selection =====
    @FXML private TextField headPositionField;
    @FXML private TextField diskSizeField;
    @FXML private ComboBox<String> directionSelector;
    @FXML private ComboBox<String> algorithmSelector;
    @FXML private Button runButton;

    // ===== Summary stats =====
    @FXML private VBox metricsSection;
    @FXML private Label totalMovementLabel;
    @FXML private Label averageMovementLabel;

    // ===== Disk seek graph =====
    @FXML private VBox diskMapSection;
    @FXML private VBox diskMapContainer;

    // ===== Results table =====
    @FXML private VBox resultsSection;
    @FXML private TableView<DiskStepResult> resultsTable;
    @FXML private TableColumn<DiskStepResult, Integer> colFromTrack;
    @FXML private TableColumn<DiskStepResult, Integer> colToTrack;
    @FXML private TableColumn<DiskStepResult, Integer> colMovement;
    @FXML private TableColumn<DiskStepResult, String> colType;

    // ===== Explanation =====
    @FXML private Label explanationLabel;

    private final ObservableList<DiskRequest> requestList = FXCollections.observableArrayList();
    private final Map<String, String> explanations = new HashMap<>();

    @FXML
    public void initialize() {
        setupRequestTable();
        setupExplanations();
        setupDirectionSelector();
        setupAlgorithmSelector();
        setupResultsTable();

        addRequestButton.setOnAction(e -> onAddRequest());
        removeRequestButton.setOnAction(e -> onRemoveSelectedRequest());
        clearRequestButton.setOnAction(e -> requestList.clear());
        loadRequestExampleButton.setOnAction(e -> onLoadRequestExample());

        runButton.setOnAction(e -> onRunSimulation());

        if (backButton != null) {
            backButton.setOnAction(e -> Main.switchScreen("/fxml/Dashboard.fxml"));
        }
    }

    private void setupRequestTable() {
        colTrackNumber.setCellValueFactory(new PropertyValueFactory<>("trackNumber"));
        colTrackNumber.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colTrackNumber.setOnEditCommit(e -> e.getRowValue().setTrackNumber(e.getNewValue()));

        requestTable.setItems(requestList);
        requestTable.setEditable(true);
    }

    private void setupDirectionSelector() {
        directionSelector.setItems(FXCollections.observableArrayList("Toward 0", "Toward Max"));
        directionSelector.setValue("Toward 0");
    }

    private void setupAlgorithmSelector() {
        algorithmSelector.setItems(FXCollections.observableArrayList("FCFS", "SSTF", "SCAN", "C-SCAN"));
        algorithmSelector.setValue("FCFS");

        algorithmSelector.setOnAction(e ->
                explanationLabel.setText(explanations.getOrDefault(
                        algorithmSelector.getValue(), "Select an algorithm to see its explanation."
                ))
        );

        explanationLabel.setText(explanations.getOrDefault("FCFS", ""));
    }

    private void setupResultsTable() {
        colFromTrack.setCellValueFactory(new PropertyValueFactory<>("fromTrack"));
        colToTrack.setCellValueFactory(new PropertyValueFactory<>("toTrack"));
        colMovement.setCellValueFactory(new PropertyValueFactory<>("movement"));

        colType.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().isBoundaryMove() ? "Boundary" : "Request")
        );
    }

    private void setupExplanations() {
        explanations.put("FCFS",
                "Services track requests strictly in the order they arrive, with no reordering at all. " +
                "Simple and fair in arrival order, but can cause large, wasteful head movements if requests " +
                "are scattered across the disk.");
        explanations.put("SSTF",
                "Shortest Seek Time First always services whichever remaining request is closest to the " +
                "current head position. Minimizes movement at each step, but can starve requests far from " +
                "the current cluster of activity.");
        explanations.put("SCAN",
                "The elevator algorithm: the head sweeps fully to one end of the disk, servicing requests " +
                "along the way, then reverses and sweeps fully to the other end. Avoids starvation, at the " +
                "cost of some unnecessary movement to reach each boundary.");
        explanations.put("C-SCAN",
                "Like SCAN, but never reverses direction — after reaching one end, it jumps straight back " +
                "to the opposite end and continues the same way. Gives more uniform wait times across all " +
                "requests, at the cost of the long circular jump.");
    }

    // ===== Button Actions =====

    private void onAddRequest() {
        requestList.add(new DiskRequest(0));
    }

    private void onRemoveSelectedRequest() {
        DiskRequest selected = requestTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            requestList.remove(selected);
        }
    }

    private void onLoadRequestExample() {
        requestList.clear();
        requestList.addAll(
                new DiskRequest(98), new DiskRequest(183), new DiskRequest(37), new DiskRequest(122),
                new DiskRequest(14), new DiskRequest(124), new DiskRequest(65), new DiskRequest(67)
        );
        headPositionField.setText("50");
        diskSizeField.setText("199");
    }

    private void onRunSimulation() {

        if (requestList.isEmpty()) {
            showError("Please add at least one track request before running the simulation.");
            return;
        }

        int headPosition;
        int diskSize;

        try {
            headPosition = Integer.parseInt(headPositionField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Initial head position must be a valid whole number.");
            return;
        }

        try {
            diskSize = Integer.parseInt(diskSizeField.getText().trim());
            if (diskSize <= 0) {
                showError("Disk size must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Disk size must be a valid whole number.");
            return;
        }

        if (headPosition < 0 || headPosition > diskSize) {
            showError("Initial head position must be between 0 and the disk size.");
            return;
        }

        for (DiskRequest r : requestList) {
            if (r.getTrackNumber() < 0 || r.getTrackNumber() > diskSize) {
                showError("Track " + r.getTrackNumber() + " is outside the valid disk range (0–" + diskSize + ").");
                return;
            }
        }

        List<Integer> requests = requestList.stream()
                .map(DiskRequest::getTrackNumber)
                .collect(Collectors.toList());

        boolean towardZero = "Toward 0".equals(directionSelector.getValue());

        String selectedAlgorithm = algorithmSelector.getValue();
        DiskSchedulingAlgorithm algorithm = createAlgorithm(selectedAlgorithm);

        if (algorithm == null) {
            return;
        }

        DiskSchedulingResult result = algorithm.run(headPosition, requests, diskSize, towardZero);
        displayResults(result, diskSize);
    }

    private DiskSchedulingAlgorithm createAlgorithm(String name) {
        switch (name) {
            case "FCFS":
                return new FCFS();
            case "SSTF":
                return new SSTF();
            case "SCAN":
                return new SCAN();
            case "C-SCAN":
                return new CSCAN();
            default:
                return null;
        }
    }

    private void displayResults(DiskSchedulingResult result, int diskSize) {

        totalMovementLabel.setText(result.getTotalHeadMovement() + " tracks");
        averageMovementLabel.setText(String.format("%.2f tracks", result.getAverageHeadMovement()));
        metricsSection.setVisible(true);
        metricsSection.setManaged(true);

        diskMapContainer.getChildren().clear();
        diskMapContainer.getChildren().add(DiskVisualizer.render(result.getSteps(), diskSize));
        diskMapSection.setVisible(true);
        diskMapSection.setManaged(true);

        resultsTable.setItems(FXCollections.observableArrayList(result.getSteps()));
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