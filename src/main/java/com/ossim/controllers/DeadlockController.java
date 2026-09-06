package com.ossim.controllers;

import com.ossim.Main;
import com.ossim.algorithms.deadlock.BankersAlgorithm;
import com.ossim.models.BankersResult;
import com.ossim.visualization.DeadlockVisualizer;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeadlockController {

    @FXML private TextField numProcessesField;
    @FXML private TextField numResourcesField;
    @FXML private Button generateGridButton;
    @FXML private Button loadExampleButton;
    @FXML private Button resetButton;
    @FXML private Button backButton;
    @FXML private Button runButton;

    @FXML private VBox allocationGridContainer;
    @FXML private VBox maxGridContainer;
    @FXML private HBox availableFieldsContainer;

    @FXML private VBox verdictSection;
    @FXML private Label verdictLabel;
    @FXML private Label sequenceLabel;

    @FXML private VBox matricesSection;
    @FXML private HBox matricesContainer;

    @FXML private VBox traceSection;
    @FXML private HBox traceContainer;

    private TextField[][] allocationFields;
    private TextField[][] maxFields;
    private TextField[] availableFields;
    private int currentNumProcesses;
    private int currentNumResources;

    @FXML
    public void initialize() {
        generateGridButton.setOnAction(e -> onGenerateGrid());
        loadExampleButton.setOnAction(e -> onLoadExample());
        resetButton.setOnAction(e -> onReset());
        runButton.setOnAction(e -> onRunSimulation());

        if (backButton != null) {
            backButton.setOnAction(e -> Main.switchScreen("/fxml/Dashboard.fxml"));
        }

        // Build an initial grid matching the default field values, so the
        // screen isn't empty on first load
        onGenerateGrid();
    }

    private void onGenerateGrid() {

        int numProcesses;
        int numResources;

        try {
            numProcesses = Integer.parseInt(numProcessesField.getText().trim());
            numResources = Integer.parseInt(numResourcesField.getText().trim());
            if (numProcesses <= 0 || numResources <= 0) {
                showError("Number of processes and resource types must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Number of processes and resource types must be valid whole numbers.");
            return;
        }

        currentNumProcesses = numProcesses;
        currentNumResources = numResources;

        allocationFields = new TextField[numProcesses][numResources];
        maxFields = new TextField[numProcesses][numResources];
        availableFields = new TextField[numResources];

        allocationGridContainer.getChildren().setAll(
                buildMatrixGrid(allocationFields, numProcesses, numResources));
        maxGridContainer.getChildren().setAll(
                buildMatrixGrid(maxFields, numProcesses, numResources));

        HBox availableRow = new HBox(10);
        for (int j = 0; j < numResources; j++) {
            VBox pair = new VBox(4);
            Label label = new Label("R" + j);
            label.setStyle("-fx-text-fill: #a6adc8; -fx-font-size: 11px;");
            TextField field = new TextField("0");
            field.setPrefWidth(50);
            availableFields[j] = field;
            pair.getChildren().addAll(label, field);
            availableRow.getChildren().add(pair);
        }
        availableFieldsContainer.getChildren().setAll(availableRow);
    }

    private GridPane buildMatrixGrid(TextField[][] fields, int numProcesses, int numResources) {

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);

        for (int j = 0; j < numResources; j++) {
            Label header = new Label("R" + j);
            header.setStyle("-fx-text-fill: #a6adc8; -fx-font-size: 11px; -fx-font-weight: bold;");
            grid.add(header, j + 1, 0);
        }

        for (int i = 0; i < numProcesses; i++) {
            Label rowLabel = new Label("P" + i);
            rowLabel.setStyle("-fx-text-fill: #a6adc8; -fx-font-size: 11px; -fx-font-weight: bold;");
            grid.add(rowLabel, 0, i + 1);

            for (int j = 0; j < numResources; j++) {
                TextField field = new TextField("0");
                field.setPrefWidth(50);
                fields[i][j] = field;
                grid.add(field, j + 1, i + 1);
            }
        }

        return grid;
    }

    private void onLoadExample() {
        numProcessesField.setText("5");
        numResourcesField.setText("3");
        onGenerateGrid();

        int[][] allocExample = {
                {0, 1, 0}, {2, 0, 0}, {3, 0, 2}, {2, 1, 1}, {0, 0, 2}
        };
        int[][] maxExample = {
                {7, 5, 3}, {3, 2, 2}, {9, 0, 2}, {2, 2, 2}, {4, 3, 3}
        };
        int[] availableExample = {3, 3, 2};

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                allocationFields[i][j].setText(String.valueOf(allocExample[i][j]));
                maxFields[i][j].setText(String.valueOf(maxExample[i][j]));
            }
        }
        for (int j = 0; j < 3; j++) {
            availableFields[j].setText(String.valueOf(availableExample[j]));
        }
    }

    private void onReset() {
        numProcessesField.setText("5");
        numResourcesField.setText("3");
        onGenerateGrid();

        verdictSection.setVisible(false);
        verdictSection.setManaged(false);
        matricesSection.setVisible(false);
        matricesSection.setManaged(false);
        traceSection.setVisible(false);
        traceSection.setManaged(false);
    }

    private void onRunSimulation() {

        if (allocationFields == null || maxFields == null || availableFields == null) {
            showError("Please generate the grid before running the simulation.");
            return;
        }

        List<List<Integer>> allocation = new ArrayList<>();
        List<List<Integer>> max = new ArrayList<>();
        List<Integer> available = new ArrayList<>();

        try {
            for (int i = 0; i < currentNumProcesses; i++) {
                List<Integer> allocRow = new ArrayList<>();
                List<Integer> maxRow = new ArrayList<>();
                for (int j = 0; j < currentNumResources; j++) {
                    int allocVal = Integer.parseInt(allocationFields[i][j].getText().trim());
                    int maxVal = Integer.parseInt(maxFields[i][j].getText().trim());

                    if (allocVal < 0 || maxVal < 0) {
                        showError("Allocation and Max values cannot be negative.");
                        return;
                    }
                    if (maxVal < allocVal) {
                        showError("Max cannot be less than Allocation for P" + i + ", R" + j + ".");
                        return;
                    }

                    allocRow.add(allocVal);
                    maxRow.add(maxVal);
                }
                allocation.add(allocRow);
                max.add(maxRow);
            }

            for (int j = 0; j < currentNumResources; j++) {
                int availVal = Integer.parseInt(availableFields[j].getText().trim());
                if (availVal < 0) {
                    showError("Available values cannot be negative.");
                    return;
                }
                available.add(availVal);
            }
        } catch (NumberFormatException e) {
            showError("All matrix and vector entries must be valid whole numbers.");
            return;
        }

        BankersResult result = new BankersAlgorithm()
                .run(currentNumProcesses, currentNumResources, allocation, max, available);

        displayResults(result, allocation, max, available);
    }

    private void displayResults(BankersResult result, List<List<Integer>> allocation,
                                 List<List<Integer>> max, List<Integer> available) {

        if (result.isSafe()) {
            verdictLabel.setText("SAFE");
            verdictLabel.setStyle("-fx-text-fill: #a6e3a1; -fx-font-size: 22px; -fx-font-weight: bold;");

            String sequence = result.getSafeSequence().stream()
                    .map(i -> "P" + i)
                    .collect(Collectors.joining(" → "));
            sequenceLabel.setText("Safe Sequence: " + sequence);
        } else {
            verdictLabel.setText("UNSAFE");
            verdictLabel.setStyle("-fx-text-fill: #f38ba8; -fx-font-size: 22px; -fx-font-weight: bold;");
            sequenceLabel.setText("No safe sequence exists — the system may be in or heading toward deadlock.");
        }
        verdictSection.setVisible(true);
        verdictSection.setManaged(true);

        matricesContainer.getChildren().clear();
        matricesContainer.getChildren().add(DeadlockVisualizer.renderMatrices(
                currentNumProcesses, currentNumResources, allocation, max, result.getNeedMatrix()));
        matricesSection.setVisible(true);
        matricesSection.setManaged(true);

        traceContainer.getChildren().clear();
        traceContainer.getChildren().add(DeadlockVisualizer.renderSafeSequence(result.getSteps(), available));
        traceSection.setVisible(true);
        traceSection.setManaged(true);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}