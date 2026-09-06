package com.ossim.controllers;

import com.ossim.Main;
import com.ossim.algorithms.cpu.*;
import com.ossim.algorithms.memory.*;
import com.ossim.algorithms.paging.*;
import com.ossim.algorithms.disk.SSTF;
import com.ossim.algorithms.disk.SCAN;
import com.ossim.algorithms.disk.CSCAN;
import com.ossim.algorithms.disk.DiskSchedulingAlgorithm;
import com.ossim.models.*;
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
import java.util.stream.Collectors;

public class ComparisonController {

    // ===================================================================
    // TAB 1: CPU SCHEDULING (unchanged)
    // ===================================================================

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

    // ===================================================================
    // TAB 2: MEMORY MANAGEMENT
    // ===================================================================

    @FXML private TableView<MemoryBlock> memPartitionTable;
    @FXML private TableColumn<MemoryBlock, String> memColBlockId;
    @FXML private TableColumn<MemoryBlock, Integer> memColBlockSize;
    @FXML private Button memAddPartitionButton;
    @FXML private Button memRemovePartitionButton;
    @FXML private Button memClearPartitionsButton;
    @FXML private Button memLoadPartitionExampleButton;

    @FXML private TableView<MemoryProcess> memProcessTable;
    @FXML private TableColumn<MemoryProcess, String> memColProcessId;
    @FXML private TableColumn<MemoryProcess, Integer> memColProcessSize;
    @FXML private Button memAddProcessButton;
    @FXML private Button memRemoveProcessButton;
    @FXML private Button memClearProcessesButton;
    @FXML private Button memLoadProcessExampleButton;

    @FXML private CheckBox memFirstFitCheck;
    @FXML private CheckBox memBestFitCheck;
    @FXML private CheckBox memWorstFitCheck;
    @FXML private Button memCompareButton;

    @FXML private VBox memResultsSection;
    @FXML private TableView<MemComparisonRow> memComparisonTable;
    @FXML private TableColumn<MemComparisonRow, String> memColAlgoName;
    @FXML private TableColumn<MemComparisonRow, String> memColAllocated;
    @FXML private TableColumn<MemComparisonRow, String> memColFailed;
    @FXML private TableColumn<MemComparisonRow, String> memColFragmentation;
    @FXML private TableColumn<MemComparisonRow, String> memColFreeMemory;

    @FXML private VBox memWinnerSection;
    @FXML private Label memWinnerNameLabel;
    @FXML private Label memWinnerReasonLabel;

    private final ObservableList<MemoryBlock> memPartitionList = FXCollections.observableArrayList();
    private final ObservableList<MemoryProcess> memProcessList = FXCollections.observableArrayList();

    // ===================================================================
    // TAB 3: PAGE REPLACEMENT
    // ===================================================================

    @FXML private TableView<ReferencePage> pageReferenceTable;
    @FXML private TableColumn<ReferencePage, Integer> pageColPageNumber;
    @FXML private Button pageAddRefButton;
    @FXML private Button pageRemoveRefButton;
    @FXML private Button pageClearRefButton;
    @FXML private Button pageLoadRefExampleButton;

    @FXML private TextField pageFrameCountField;
    @FXML private CheckBox pageFifoCheck;
    @FXML private CheckBox pageLruCheck;
    @FXML private CheckBox pageOptimalCheck;
    @FXML private Button pageCompareButton;

    @FXML private VBox pageResultsSection;
    @FXML private TableView<PageComparisonRow> pageComparisonTable;
    @FXML private TableColumn<PageComparisonRow, String> pageColAlgoName;
    @FXML private TableColumn<PageComparisonRow, String> pageColHits;
    @FXML private TableColumn<PageComparisonRow, String> pageColFaults;
    @FXML private TableColumn<PageComparisonRow, String> pageColHitRatio;
    @FXML private TableColumn<PageComparisonRow, String> pageColFaultRatio;

    @FXML private VBox pageWinnerSection;
    @FXML private Label pageWinnerNameLabel;
    @FXML private Label pageWinnerReasonLabel;

    private final ObservableList<ReferencePage> pageReferenceList = FXCollections.observableArrayList();

    // ===================================================================
    // TAB 4: DISK SCHEDULING
    // ===================================================================

    @FXML private TableView<DiskRequest> diskRequestTable;
    @FXML private TableColumn<DiskRequest, Integer> diskColTrackNumber;
    @FXML private Button diskAddRequestButton;
    @FXML private Button diskRemoveRequestButton;
    @FXML private Button diskClearRequestButton;
    @FXML private Button diskLoadRequestExampleButton;

    @FXML private TextField diskHeadPositionField;
    @FXML private TextField diskSizeField;
    @FXML private ComboBox<String> diskDirectionSelector;
    @FXML private CheckBox diskFcfsCheck;
    @FXML private CheckBox diskSstfCheck;
    @FXML private CheckBox diskScanCheck;
    @FXML private CheckBox diskCscanCheck;
    @FXML private Button diskCompareButton;

    @FXML private VBox diskResultsSection;
    @FXML private TableView<DiskComparisonRow> diskComparisonTable;
    @FXML private TableColumn<DiskComparisonRow, String> diskColAlgoName;
    @FXML private TableColumn<DiskComparisonRow, String> diskColTotalMovement;
    @FXML private TableColumn<DiskComparisonRow, String> diskColAvgMovement;

    @FXML private VBox diskWinnerSection;
    @FXML private Label diskWinnerNameLabel;
    @FXML private Label diskWinnerReasonLabel;

    private final ObservableList<DiskRequest> diskRequestList = FXCollections.observableArrayList();

    // ===================================================================
    // INITIALIZE
    // ===================================================================

    @FXML
    public void initialize() {
        if (backButton != null) {
            backButton.setOnAction(e -> Main.switchScreen("/fxml/Dashboard.fxml"));
        }

        // --- CPU tab (unchanged) ---
        setupProcessTable();
        setupComparisonTable();
        addProcessButton.setOnAction(e -> onAddProcess());
        removeProcessButton.setOnAction(e -> onRemoveSelectedProcess());
        clearButton.setOnAction(e -> onClearAll());
        loadExampleButton.setOnAction(e -> onLoadExample());
        compareButton.setOnAction(e -> onCompare());

        // --- Memory tab ---
        setupMemPartitionTable();
        setupMemProcessTable();
        setupMemComparisonTable();
        memAddPartitionButton.setOnAction(e -> onMemAddPartition());
        memRemovePartitionButton.setOnAction(e -> onMemRemoveSelectedPartition());
        memClearPartitionsButton.setOnAction(e -> onMemClearPartitions());
        memLoadPartitionExampleButton.setOnAction(e -> onMemLoadPartitionExample());
        memAddProcessButton.setOnAction(e -> onMemAddProcess());
        memRemoveProcessButton.setOnAction(e -> onMemRemoveSelectedProcess());
        memClearProcessesButton.setOnAction(e -> onMemClearProcesses());
        memLoadProcessExampleButton.setOnAction(e -> onMemLoadProcessExample());
        memCompareButton.setOnAction(e -> onMemCompare());

        // --- Page Replacement tab ---
        setupPageReferenceTable();
        setupPageComparisonTable();
        pageAddRefButton.setOnAction(e -> onPageAddRef());
        pageRemoveRefButton.setOnAction(e -> onPageRemoveSelectedRef());
        pageClearRefButton.setOnAction(e -> onPageClearAll());
        pageLoadRefExampleButton.setOnAction(e -> onPageLoadRefExample());
        pageCompareButton.setOnAction(e -> onPageCompare());

        // --- Disk Scheduling tab ---
        setupDiskRequestTable();
        setupDiskComparisonTable();
        diskDirectionSelector.setItems(FXCollections.observableArrayList("Toward 0", "Toward Max"));
        diskDirectionSelector.setValue("Toward 0");
        diskAddRequestButton.setOnAction(e -> onDiskAddRequest());
        diskRemoveRequestButton.setOnAction(e -> onDiskRemoveSelectedRequest());
        diskClearRequestButton.setOnAction(e -> onDiskClearAll());
        diskLoadRequestExampleButton.setOnAction(e -> onDiskLoadRequestExample());
        diskCompareButton.setOnAction(e -> onDiskCompare());
    }

    // ===================================================================
    // TAB 1: CPU SCHEDULING METHODS (unchanged)
    // ===================================================================

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

    // ===================================================================
    // TAB 2: MEMORY MANAGEMENT METHODS
    // ===================================================================

    private void setupMemPartitionTable() {
        memColBlockId.setCellValueFactory(new PropertyValueFactory<>("blockId"));
        memColBlockSize.setCellValueFactory(new PropertyValueFactory<>("size"));

        memColBlockId.setCellFactory(TextFieldTableCell.forTableColumn());
        memColBlockSize.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        memColBlockId.setOnEditCommit(e -> e.getRowValue().setBlockId(e.getNewValue()));
        memColBlockSize.setOnEditCommit(e -> e.getRowValue().setSize(e.getNewValue()));

        memPartitionTable.setItems(memPartitionList);
        memPartitionTable.setEditable(true);
    }

    private void setupMemProcessTable() {
        memColProcessId.setCellValueFactory(new PropertyValueFactory<>("processId"));
        memColProcessSize.setCellValueFactory(new PropertyValueFactory<>("size"));

        memColProcessId.setCellFactory(TextFieldTableCell.forTableColumn());
        memColProcessSize.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        memColProcessId.setOnEditCommit(e -> e.getRowValue().setProcessId(e.getNewValue()));
        memColProcessSize.setOnEditCommit(e -> e.getRowValue().setSize(e.getNewValue()));

        memProcessTable.setItems(memProcessList);
        memProcessTable.setEditable(true);
    }

    private void setupMemComparisonTable() {
        memColAlgoName.setCellValueFactory(new PropertyValueFactory<>("algorithmName"));
        memColAllocated.setCellValueFactory(new PropertyValueFactory<>("allocated"));
        memColFailed.setCellValueFactory(new PropertyValueFactory<>("failed"));
        memColFragmentation.setCellValueFactory(new PropertyValueFactory<>("fragmentation"));
        memColFreeMemory.setCellValueFactory(new PropertyValueFactory<>("freeMemory"));
    }

    private void onMemAddPartition() {
        int nextNumber = memPartitionList.size() + 1;
        memPartitionList.add(new MemoryBlock("B" + nextNumber, 100));
    }

    private void onMemRemoveSelectedPartition() {
        MemoryBlock selected = memPartitionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            memPartitionList.remove(selected);
        }
    }

    private void onMemLoadPartitionExample() {
        memPartitionList.clear();
        memPartitionList.addAll(
                new MemoryBlock("B1", 100), new MemoryBlock("B2", 500), new MemoryBlock("B3", 200),
                new MemoryBlock("B4", 300), new MemoryBlock("B5", 600)
        );
    }

    private void onMemAddProcess() {
        int nextNumber = memProcessList.size() + 1;
        memProcessList.add(new MemoryProcess("P" + nextNumber, 100));
    }

    private void onMemRemoveSelectedProcess() {
        MemoryProcess selected = memProcessTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            memProcessList.remove(selected);
        }
    }

    private void onMemLoadProcessExample() {
        memProcessList.clear();
        memProcessList.addAll(
                new MemoryProcess("P1", 212), new MemoryProcess("P2", 417),
                new MemoryProcess("P3", 112), new MemoryProcess("P4", 426)
        );
    }

    private void onMemCompare() {

        if (memPartitionList.isEmpty()) {
            showError("Please add at least one partition before comparing.");
            return;
        }
        for (MemoryBlock b : memPartitionList) {
            if (b.getSize() <= 0) {
                showError("Partition size must be greater than zero for block " + b.getBlockId());
                return;
            }
        }
        if (memProcessList.isEmpty()) {
            showError("Please add at least one process before comparing.");
            return;
        }
        for (MemoryProcess p : memProcessList) {
            if (p.getSize() <= 0) {
                showError("Process size must be greater than zero for process " + p.getProcessId());
                return;
            }
        }

        List<MemoryAllocationAlgorithm> selectedAlgorithms = new ArrayList<>();
        if (memFirstFitCheck.isSelected()) selectedAlgorithms.add(new FirstFit());
        if (memBestFitCheck.isSelected()) selectedAlgorithms.add(new BestFit());
        if (memWorstFitCheck.isSelected()) selectedAlgorithms.add(new WorstFit());

        if (selectedAlgorithms.isEmpty()) {
            showError("Please select at least one algorithm to compare.");
            return;
        }

        List<MemComparisonRow> rows = new ArrayList<>();
        String bestAlgorithmName = null;
        int bestFailed = Integer.MAX_VALUE;
        int bestFragmentation = Integer.MAX_VALUE;

        for (MemoryAllocationAlgorithm algorithm : selectedAlgorithms) {
            MemoryAllocationResult result = algorithm.run(memPartitionList, memProcessList);

            rows.add(new MemComparisonRow(
                    algorithm.getName(),
                    String.valueOf(result.getAllocatedCount()),
                    String.valueOf(result.getFailedCount()),
                    result.getTotalInternalFragmentation() + " MB",
                    result.getTotalFreeMemory() + " MB"
            ));

            int failed = result.getFailedCount();
            int fragmentation = result.getTotalInternalFragmentation();
            if (failed < bestFailed || (failed == bestFailed && fragmentation < bestFragmentation)) {
                bestFailed = failed;
                bestFragmentation = fragmentation;
                bestAlgorithmName = algorithm.getName();
            }
        }

        memComparisonTable.setItems(FXCollections.observableArrayList(rows));
        memResultsSection.setVisible(true);
        memResultsSection.setManaged(true);

        memWinnerNameLabel.setText(bestAlgorithmName);
        memWinnerReasonLabel.setText(String.format(
                "Fewest Failed Allocations (%d), Lowest Internal Fragmentation Among Ties (%d MB)",
                bestFailed, bestFragmentation));
        memWinnerSection.setVisible(true);
        memWinnerSection.setManaged(true);
    }

    // ===================================================================
    // TAB 3: PAGE REPLACEMENT METHODS
    // ===================================================================

    private void setupPageReferenceTable() {
        pageColPageNumber.setCellValueFactory(new PropertyValueFactory<>("pageNumber"));
        pageColPageNumber.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        pageColPageNumber.setOnEditCommit(e -> e.getRowValue().setPageNumber(e.getNewValue()));

        pageReferenceTable.setItems(pageReferenceList);
        pageReferenceTable.setEditable(true);
    }

    private void setupPageComparisonTable() {
        pageColAlgoName.setCellValueFactory(new PropertyValueFactory<>("algorithmName"));
        pageColHits.setCellValueFactory(new PropertyValueFactory<>("hits"));
        pageColFaults.setCellValueFactory(new PropertyValueFactory<>("faults"));
        pageColHitRatio.setCellValueFactory(new PropertyValueFactory<>("hitRatio"));
        pageColFaultRatio.setCellValueFactory(new PropertyValueFactory<>("faultRatio"));
    }

    private void onPageAddRef() {
        pageReferenceList.add(new ReferencePage(0));
    }

    private void onPageRemoveSelectedRef() {
        ReferencePage selected = pageReferenceTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            pageReferenceList.remove(selected);
        }
    }

    private void onPageLoadRefExample() {
        pageReferenceList.clear();
        pageReferenceList.addAll(
                new ReferencePage(7), new ReferencePage(0), new ReferencePage(1), new ReferencePage(2),
                new ReferencePage(0), new ReferencePage(3), new ReferencePage(0), new ReferencePage(4)
        );
    }

    private void onPageCompare() {

        if (pageReferenceList.isEmpty()) {
            showError("Please add at least one page reference before comparing.");
            return;
        }
        for (ReferencePage p : pageReferenceList) {
            if (p.getPageNumber() < 0) {
                showError("Page numbers cannot be negative.");
                return;
            }
        }

        int frameCount;
        try {
            frameCount = Integer.parseInt(pageFrameCountField.getText().trim());
            if (frameCount <= 0) {
                showError("Frame count must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Frame count must be a valid whole number.");
            return;
        }

        List<Integer> referenceString = pageReferenceList.stream()
                .map(ReferencePage::getPageNumber)
                .collect(Collectors.toList());

        List<PageReplacementAlgorithm> selectedAlgorithms = new ArrayList<>();
        if (pageFifoCheck.isSelected()) selectedAlgorithms.add(new FIFO());
        if (pageLruCheck.isSelected()) selectedAlgorithms.add(new LRU());
        if (pageOptimalCheck.isSelected()) selectedAlgorithms.add(new Optimal());

        if (selectedAlgorithms.isEmpty()) {
            showError("Please select at least one algorithm to compare.");
            return;
        }

        List<PageComparisonRow> rows = new ArrayList<>();
        String bestAlgorithmName = null;
        int bestFaults = Integer.MAX_VALUE;

        for (PageReplacementAlgorithm algorithm : selectedAlgorithms) {
            PageReplacementResult result = algorithm.run(referenceString, frameCount);

            rows.add(new PageComparisonRow(
                    algorithm.getName(),
                    String.valueOf(result.getHitCount()),
                    String.valueOf(result.getFaultCount()),
                    String.format("%.2f", result.getHitRatio()),
                    String.format("%.2f", result.getFaultRatio())
            ));

            if (result.getFaultCount() < bestFaults) {
                bestFaults = result.getFaultCount();
                bestAlgorithmName = algorithm.getName();
            }
        }

        pageComparisonTable.setItems(FXCollections.observableArrayList(rows));
        pageResultsSection.setVisible(true);
        pageResultsSection.setManaged(true);

        pageWinnerNameLabel.setText(bestAlgorithmName);
        pageWinnerReasonLabel.setText(String.format("Fewest Page Faults (%d)", bestFaults));
        pageWinnerSection.setVisible(true);
        pageWinnerSection.setManaged(true);
    }

    // ===================================================================
    // TAB 4: DISK SCHEDULING METHODS
    // ===================================================================

    private void setupDiskRequestTable() {
        diskColTrackNumber.setCellValueFactory(new PropertyValueFactory<>("trackNumber"));
        diskColTrackNumber.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        diskColTrackNumber.setOnEditCommit(e -> e.getRowValue().setTrackNumber(e.getNewValue()));

        diskRequestTable.setItems(diskRequestList);
        diskRequestTable.setEditable(true);
    }

    private void setupDiskComparisonTable() {
        diskColAlgoName.setCellValueFactory(new PropertyValueFactory<>("algorithmName"));
        diskColTotalMovement.setCellValueFactory(new PropertyValueFactory<>("totalMovement"));
        diskColAvgMovement.setCellValueFactory(new PropertyValueFactory<>("avgMovement"));
    }

    private void onDiskAddRequest() {
        diskRequestList.add(new DiskRequest(0));
    }

    private void onDiskRemoveSelectedRequest() {
        DiskRequest selected = diskRequestTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            diskRequestList.remove(selected);
        }
    }

    private void onDiskLoadRequestExample() {
        diskRequestList.clear();
        diskRequestList.addAll(
                new DiskRequest(98), new DiskRequest(183), new DiskRequest(37), new DiskRequest(122),
                new DiskRequest(14), new DiskRequest(124), new DiskRequest(65), new DiskRequest(67)
        );
        diskHeadPositionField.setText("50");
        diskSizeField.setText("199");
    }

        private void onClearAll() {
        processList.clear();
        resultsSection.setVisible(false);
        resultsSection.setManaged(false);
        winnerSection.setVisible(false);
        winnerSection.setManaged(false);
    }

    private void onMemClearPartitions() {
        memPartitionList.clear();
        hideMemResults();
    }

    private void onMemClearProcesses() {
        memProcessList.clear();
        hideMemResults();
    }

    private void hideMemResults() {
        memResultsSection.setVisible(false);
        memResultsSection.setManaged(false);
        memWinnerSection.setVisible(false);
        memWinnerSection.setManaged(false);
    }

    private void onPageClearAll() {
        pageReferenceList.clear();
        pageResultsSection.setVisible(false);
        pageResultsSection.setManaged(false);
        pageWinnerSection.setVisible(false);
        pageWinnerSection.setManaged(false);
    }

    private void onDiskClearAll() {
        diskRequestList.clear();
        diskResultsSection.setVisible(false);
        diskResultsSection.setManaged(false);
        diskWinnerSection.setVisible(false);
        diskWinnerSection.setManaged(false);
    }

    private void onDiskCompare() {

        if (diskRequestList.isEmpty()) {
            showError("Please add at least one track request before comparing.");
            return;
        }

        int headPosition;
        int diskSize;

        try {
            headPosition = Integer.parseInt(diskHeadPositionField.getText().trim());
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

        for (DiskRequest r : diskRequestList) {
            if (r.getTrackNumber() < 0 || r.getTrackNumber() > diskSize) {
                showError("Track " + r.getTrackNumber() + " is outside the valid disk range (0–" + diskSize + ").");
                return;
            }
        }

        List<Integer> requests = diskRequestList.stream()
                .map(DiskRequest::getTrackNumber)
                .collect(Collectors.toList());

        boolean towardZero = "Toward 0".equals(diskDirectionSelector.getValue());

        List<DiskSchedulingAlgorithm> selectedAlgorithms = new ArrayList<>();
        if (diskFcfsCheck.isSelected()) selectedAlgorithms.add(new com.ossim.algorithms.disk.FCFS());
        if (diskSstfCheck.isSelected()) selectedAlgorithms.add(new SSTF());
        if (diskScanCheck.isSelected()) selectedAlgorithms.add(new SCAN());
        if (diskCscanCheck.isSelected()) selectedAlgorithms.add(new CSCAN());

        if (selectedAlgorithms.isEmpty()) {
            showError("Please select at least one algorithm to compare.");
            return;
        }

        List<DiskComparisonRow> rows = new ArrayList<>();
        String bestAlgorithmName = null;
        int bestTotalMovement = Integer.MAX_VALUE;

        for (DiskSchedulingAlgorithm algorithm : selectedAlgorithms) {
            DiskSchedulingResult result = algorithm.run(headPosition, requests, diskSize, towardZero);

            rows.add(new DiskComparisonRow(
                    algorithm.getName(),
                    result.getTotalHeadMovement() + " tracks",
                    String.format("%.2f tracks", result.getAverageHeadMovement())
            ));

            if (result.getTotalHeadMovement() < bestTotalMovement) {
                bestTotalMovement = result.getTotalHeadMovement();
                bestAlgorithmName = algorithm.getName();
            }
        }

        diskComparisonTable.setItems(FXCollections.observableArrayList(rows));
        diskResultsSection.setVisible(true);
        diskResultsSection.setManaged(true);

        diskWinnerNameLabel.setText(bestAlgorithmName);
        diskWinnerReasonLabel.setText(String.format("Lowest Total Head Movement (%d tracks)", bestTotalMovement));
        diskWinnerSection.setVisible(true);
        diskWinnerSection.setManaged(true);
    }

    // ===================================================================
    // SHARED
    // ===================================================================

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ===================================================================
    // ROW CLASSES
    // ===================================================================

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

    public static class MemComparisonRow {
        private final String algorithmName;
        private final String allocated;
        private final String failed;
        private final String fragmentation;
        private final String freeMemory;

        public MemComparisonRow(String algorithmName, String allocated, String failed,
                                 String fragmentation, String freeMemory) {
            this.algorithmName = algorithmName;
            this.allocated = allocated;
            this.failed = failed;
            this.fragmentation = fragmentation;
            this.freeMemory = freeMemory;
        }

        public String getAlgorithmName() { return algorithmName; }
        public String getAllocated() { return allocated; }
        public String getFailed() { return failed; }
        public String getFragmentation() { return fragmentation; }
        public String getFreeMemory() { return freeMemory; }
    }

    public static class PageComparisonRow {
        private final String algorithmName;
        private final String hits;
        private final String faults;
        private final String hitRatio;
        private final String faultRatio;

        public PageComparisonRow(String algorithmName, String hits, String faults,
                                  String hitRatio, String faultRatio) {
            this.algorithmName = algorithmName;
            this.hits = hits;
            this.faults = faults;
            this.hitRatio = hitRatio;
            this.faultRatio = faultRatio;
        }

        public String getAlgorithmName() { return algorithmName; }
        public String getHits() { return hits; }
        public String getFaults() { return faults; }
        public String getHitRatio() { return hitRatio; }
        public String getFaultRatio() { return faultRatio; }
    }

    public static class DiskComparisonRow {
        private final String algorithmName;
        private final String totalMovement;
        private final String avgMovement;

        public DiskComparisonRow(String algorithmName, String totalMovement, String avgMovement) {
            this.algorithmName = algorithmName;
            this.totalMovement = totalMovement;
            this.avgMovement = avgMovement;
        }

        public String getAlgorithmName() { return algorithmName; }
        public String getTotalMovement() { return totalMovement; }
        public String getAvgMovement() { return avgMovement; }
    }
}