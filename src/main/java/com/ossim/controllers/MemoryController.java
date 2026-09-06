package com.ossim.controllers;

import com.ossim.Main;
import com.ossim.algorithms.memory.*;
import com.ossim.models.MemoryAllocationResult;
import com.ossim.models.MemoryBlock;
import com.ossim.models.MemoryProcess;
import com.ossim.models.MemoryProcessResult;
import com.ossim.visualization.MemoryVisualizer;
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

public class MemoryController {

    // ===== Partition input =====
    @FXML private TableView<MemoryBlock> partitionTable;
    @FXML private TableColumn<MemoryBlock, String> colBlockId;
    @FXML private TableColumn<MemoryBlock, Integer> colBlockSize;

    @FXML private Button addPartitionButton;
    @FXML private Button removePartitionButton;
    @FXML private Button clearPartitionsButton;
    @FXML private Button loadPartitionExampleButton;

    // ===== Process input =====
    @FXML private TableView<MemoryProcess> memProcessTable;
    @FXML private TableColumn<MemoryProcess, String> colMemProcessId;
    @FXML private TableColumn<MemoryProcess, Integer> colMemProcessSize;

    @FXML private Button addMemProcessButton;
    @FXML private Button removeMemProcessButton;
    @FXML private Button clearMemProcessesButton;
    @FXML private Button loadMemProcessExampleButton;

    @FXML private Button backButton;

    // ===== Algorithm selection =====
    @FXML private ComboBox<String> algorithmSelector;
    @FXML private Button runButton;

    // ===== Summary stats =====
    @FXML private VBox metricsSection;
    @FXML private Label allocatedCountLabel;
    @FXML private Label failedCountLabel;
    @FXML private Label totalFragLabel;
    @FXML private Label totalFreeLabel;

    // ===== Memory map =====
    @FXML private VBox memoryMapSection;
    @FXML private VBox memoryMapContainer;

    // ===== Results table =====
    @FXML private VBox resultsSection;
    @FXML private TableView<MemoryProcessResult> resultsTable;
    @FXML private TableColumn<MemoryProcessResult, String> colResultProcessId;
    @FXML private TableColumn<MemoryProcessResult, Integer> colResultSize;
    @FXML private TableColumn<MemoryProcessResult, String> colResultAllocated;
    @FXML private TableColumn<MemoryProcessResult, String> colResultBlockId;
    @FXML private TableColumn<MemoryProcessResult, Integer> colResultFragmentation;

    // ===== Explanation =====
    @FXML private Label explanationLabel;

    private final ObservableList<MemoryBlock> partitionList = FXCollections.observableArrayList();
    private final ObservableList<MemoryProcess> memProcessList = FXCollections.observableArrayList();
    private final Map<String, String> explanations = new HashMap<>();

    @FXML
    public void initialize() {
        setupPartitionTable();
        setupMemProcessTable();
        setupExplanations();
        setupAlgorithmSelector();
        setupResultsTable();

        addPartitionButton.setOnAction(e -> onAddPartition());
        removePartitionButton.setOnAction(e -> onRemoveSelectedPartition());
        clearPartitionsButton.setOnAction(e -> partitionList.clear());
        loadPartitionExampleButton.setOnAction(e -> onLoadPartitionExample());

        addMemProcessButton.setOnAction(e -> onAddMemProcess());
        removeMemProcessButton.setOnAction(e -> onRemoveSelectedMemProcess());
        clearMemProcessesButton.setOnAction(e -> memProcessList.clear());
        loadMemProcessExampleButton.setOnAction(e -> onLoadMemProcessExample());

        runButton.setOnAction(e -> onRunSimulation());

        if (backButton != null) {
            backButton.setOnAction(e -> Main.switchScreen("/fxml/Dashboard.fxml"));
        }
    }

    private void setupPartitionTable() {
        colBlockId.setCellValueFactory(new PropertyValueFactory<>("blockId"));
        colBlockSize.setCellValueFactory(new PropertyValueFactory<>("size"));

        colBlockId.setCellFactory(TextFieldTableCell.forTableColumn());
        colBlockSize.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        colBlockId.setOnEditCommit(e -> e.getRowValue().setBlockId(e.getNewValue()));
        colBlockSize.setOnEditCommit(e -> e.getRowValue().setSize(e.getNewValue()));

        partitionTable.setItems(partitionList);
        partitionTable.setEditable(true);
    }

    private void setupMemProcessTable() {
        colMemProcessId.setCellValueFactory(new PropertyValueFactory<>("processId"));
        colMemProcessSize.setCellValueFactory(new PropertyValueFactory<>("size"));

        colMemProcessId.setCellFactory(TextFieldTableCell.forTableColumn());
        colMemProcessSize.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        colMemProcessId.setOnEditCommit(e -> e.getRowValue().setProcessId(e.getNewValue()));
        colMemProcessSize.setOnEditCommit(e -> e.getRowValue().setSize(e.getNewValue()));

        memProcessTable.setItems(memProcessList);
        memProcessTable.setEditable(true);
    }

    private void setupAlgorithmSelector() {
        algorithmSelector.setItems(FXCollections.observableArrayList(
                "First Fit", "Best Fit", "Worst Fit"
        ));
        algorithmSelector.setValue("First Fit");

        algorithmSelector.setOnAction(e ->
                explanationLabel.setText(explanations.getOrDefault(
                        algorithmSelector.getValue(), "Select an algorithm to see its explanation."
                ))
        );

        explanationLabel.setText(explanations.getOrDefault("First Fit", ""));
    }

    private void setupResultsTable() {
        colResultProcessId.setCellValueFactory(new PropertyValueFactory<>("processId"));
        colResultSize.setCellValueFactory(new PropertyValueFactory<>("processSize"));

        colResultAllocated.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().isAllocated() ? "Yes" : "No")
        );

        colResultBlockId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getBlockId() != null ? data.getValue().getBlockId() : "-"
                )
        );

        colResultFragmentation.setCellValueFactory(new PropertyValueFactory<>("internalFragmentation"));
    }

    private void setupExplanations() {
                explanations.put("First Fit",
                "Scans partitions in the given order and allocates the process to the first partition " +
                "large enough to hold it. Fast, but can leave awkward gaps early in memory unused. " +
                "Time complexity is O(n) per allocation. Favored when allocation speed matters more than " +
                "minimizing wasted space, such as simple embedded allocators.");
        explanations.put("Best Fit",
                "Scans all partitions and picks the smallest one that still fits the process, minimizing " +
                "leftover space in that block. Tends to create many small, hard-to-use fragments over time. " +
                "Time complexity is O(n) per allocation, scanning every block to find the closest fit. " +
                "Suited to memory-constrained systems where minimizing waste per allocation matters more " +
                "than speed.");
        explanations.put("Worst Fit",
                "Scans all partitions and picks the largest available one, leaving the biggest possible " +
                "leftover for future processes. Wastes more memory per allocation, but keeps large blocks " +
                "in reserve longer. Time complexity is O(n) per allocation. Rarely used in practice since it " +
                "burns through large blocks quickly, but conceptually useful for understanding fragmentation " +
                "trade-offs.");
    }

    // ===== Button Actions =====

    private void onAddPartition() {
        int nextNumber = partitionList.size() + 1;
        partitionList.add(new MemoryBlock("B" + nextNumber, 100));
    }

    private void onRemoveSelectedPartition() {
        MemoryBlock selected = partitionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            partitionList.remove(selected);
        }
    }

    private void onLoadPartitionExample() {
        partitionList.clear();
        partitionList.addAll(
                new MemoryBlock("B1", 100),
                new MemoryBlock("B2", 500),
                new MemoryBlock("B3", 200),
                new MemoryBlock("B4", 300),
                new MemoryBlock("B5", 600)
        );
    }

    private void onAddMemProcess() {
        int nextNumber = memProcessList.size() + 1;
        memProcessList.add(new MemoryProcess("P" + nextNumber, 100));
    }

    private void onRemoveSelectedMemProcess() {
        MemoryProcess selected = memProcessTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            memProcessList.remove(selected);
        }
    }

    private void onLoadMemProcessExample() {
        memProcessList.clear();
        memProcessList.addAll(
                new MemoryProcess("P1", 212),
                new MemoryProcess("P2", 417),
                new MemoryProcess("P3", 112),
                new MemoryProcess("P4", 426)
        );
    }

    private void onRunSimulation() {

        if (partitionList.isEmpty()) {
            showError("Please add at least one partition before running the simulation.");
            return;
        }

        for (MemoryBlock b : partitionList) {
            if (b.getSize() <= 0) {
                showError("Partition size must be greater than zero for block " + b.getBlockId());
                return;
            }
        }

        if (memProcessList.isEmpty()) {
            showError("Please add at least one process before running the simulation.");
            return;
        }

        for (MemoryProcess p : memProcessList) {
            if (p.getSize() <= 0) {
                showError("Process size must be greater than zero for process " + p.getProcessId());
                return;
            }
        }

        String selectedAlgorithm = algorithmSelector.getValue();
        MemoryAllocationAlgorithm algorithm = createAlgorithm(selectedAlgorithm);

        if (algorithm == null) {
            return;
        }

        MemoryAllocationResult result = algorithm.run(partitionList, memProcessList);
        displayResults(result);
    }

    private MemoryAllocationAlgorithm createAlgorithm(String name) {
        switch (name) {
            case "First Fit":
                return new FirstFit();
            case "Best Fit":
                return new BestFit();
            case "Worst Fit":
                return new WorstFit();
            default:
                return null;
        }
    }

    private void displayResults(MemoryAllocationResult result) {

        allocatedCountLabel.setText(String.valueOf(result.getAllocatedCount()));
        failedCountLabel.setText(String.valueOf(result.getFailedCount()));
        totalFragLabel.setText(result.getTotalInternalFragmentation() + " MB");
        totalFreeLabel.setText(result.getTotalFreeMemory() + " MB");
        metricsSection.setVisible(true);
        metricsSection.setManaged(true);

        memoryMapContainer.getChildren().clear();
        memoryMapContainer.getChildren().add(MemoryVisualizer.render(result.getFinalBlockState()));
        memoryMapSection.setVisible(true);
        memoryMapSection.setManaged(true);

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