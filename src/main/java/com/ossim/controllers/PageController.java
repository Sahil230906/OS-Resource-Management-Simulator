package com.ossim.controllers;

import com.ossim.Main;
import com.ossim.algorithms.paging.*;
import com.ossim.models.PageReplacementResult;
import com.ossim.models.PageStepResult;
import com.ossim.models.ReferencePage;
import com.ossim.visualization.PageReplacementVisualizer;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PageController {

    // ===== Reference string input =====
    @FXML private TableView<ReferencePage> referenceTable;
    @FXML private TableColumn<ReferencePage, Integer> colPageNumber;

    @FXML private Button addRefButton;
    @FXML private Button removeRefButton;
    @FXML private Button clearRefButton;
    @FXML private Button loadRefExampleButton;
    @FXML private Button backButton;

    // ===== Frame count + algorithm selection =====
    @FXML private TextField frameCountField;
    @FXML private ComboBox<String> algorithmSelector;
    @FXML private Button runButton;

    // ===== Summary stats =====
    @FXML private VBox metricsSection;
    @FXML private Label hitCountLabel;
    @FXML private Label faultCountLabel;
    @FXML private Label hitRatioLabel;
    @FXML private Label faultRatioLabel;

    // ===== Frame timeline =====
    @FXML private VBox timelineSection;
    @FXML private HBox timelineContainer;

    // ===== Results table =====
    @FXML private VBox resultsSection;
    @FXML private TableView<PageStepResult> resultsTable;
    @FXML private TableColumn<PageStepResult, Integer> colStepPage;
    @FXML private TableColumn<PageStepResult, String> colStepOutcome;
    @FXML private TableColumn<PageStepResult, String> colStepFrames;
    @FXML private TableColumn<PageStepResult, String> colStepEvicted;

    // ===== Explanation =====
    @FXML private Label explanationLabel;

    private final ObservableList<ReferencePage> referenceList = FXCollections.observableArrayList();
    private final Map<String, String> explanations = new HashMap<>();

    @FXML
    public void initialize() {
        setupReferenceTable();
        setupExplanations();
        setupAlgorithmSelector();
        setupResultsTable();

        addRefButton.setOnAction(e -> onAddRef());
        removeRefButton.setOnAction(e -> onRemoveSelectedRef());
        clearRefButton.setOnAction(e -> onClearAll());
        loadRefExampleButton.setOnAction(e -> onLoadRefExample());

        runButton.setOnAction(e -> onRunSimulation());

        if (backButton != null) {
            backButton.setOnAction(e -> Main.switchScreen("/fxml/Dashboard.fxml"));
        }
    }

    private void setupReferenceTable() {
        colPageNumber.setCellValueFactory(new PropertyValueFactory<>("pageNumber"));
        colPageNumber.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colPageNumber.setOnEditCommit(e -> e.getRowValue().setPageNumber(e.getNewValue()));

        referenceTable.setItems(referenceList);
        referenceTable.setEditable(true);
    }

    private void setupAlgorithmSelector() {
        algorithmSelector.setItems(FXCollections.observableArrayList("FIFO", "LRU", "Optimal"));
        algorithmSelector.setValue("FIFO");

        algorithmSelector.setOnAction(e ->
                explanationLabel.setText(explanations.getOrDefault(
                        algorithmSelector.getValue(), "Select an algorithm to see its explanation."
                ))
        );

        explanationLabel.setText(explanations.getOrDefault("FIFO", ""));
    }

    private void setupResultsTable() {
        colStepPage.setCellValueFactory(new PropertyValueFactory<>("pageRequested"));

        colStepOutcome.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().isHit() ? "HIT" : "FAULT")
        );

        colStepFrames.setCellValueFactory(data ->
                new SimpleStringProperty(formatFrameState(data.getValue().getFrameState()))
        );

        colStepEvicted.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getEvictedPage() != null
                                ? String.valueOf(data.getValue().getEvictedPage())
                                : "-"
                )
        );
    }

    private String formatFrameState(List<Integer> frameState) {
        return frameState.stream()
                .map(p -> p == null ? "-" : String.valueOf(p))
                .collect(Collectors.joining(", "));
    }

    private void setupExplanations() {
               explanations.put("FIFO",
                "First In First Out evicts whichever page has been in memory the longest, regardless of " +
                "how recently it was used. Simple to implement, but can evict a page that's about to be " +
                "reused (Belady's anomaly can even make more frames perform worse). Time complexity is O(1) " +
                "per reference using a queue. Simple but rarely used alone in real systems, since it ignores " +
                "how recently a page was actually used.");
        explanations.put("LRU",
                "Least Recently Used evicts the page that hasn't been accessed for the longest time. " +
                "Exploits temporal locality — pages used recently are likely to be used again soon — but " +
                "needs extra bookkeeping to track access history. True LRU is O(1) per reference with the " +
                "right data structures (a hash map plus a doubly linked list), though naive implementations " +
                "are O(n). Real operating systems, including Linux, approximate LRU in hardware via " +
                "clock/second-chance algorithms rather than tracking exact access order, since perfect LRU " +
                "is expensive to maintain.");
        explanations.put("Optimal",
                "Evicts whichever page won't be needed for the longest time in the future, requiring full " +
                "knowledge of the reference string ahead of time. Provably produces the fewest possible " +
                "faults, but is impossible to implement in a real OS — used here purely as a benchmark. It " +
                "requires knowing the entire future reference string, which is impossible in a running " +
                "system — it exists purely as a theoretical yardstick to measure how close FIFO or LRU come " +
                "to the best possible result.");
    }

    // ===== Button Actions =====

    private void onAddRef() {
        referenceList.add(new ReferencePage(0));
    }

    private void onRemoveSelectedRef() {
        ReferencePage selected = referenceTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            referenceList.remove(selected);
        }
    }

    private void onLoadRefExample() {
        referenceList.clear();
        referenceList.addAll(
                new ReferencePage(7), new ReferencePage(0), new ReferencePage(1), new ReferencePage(2),
                new ReferencePage(0), new ReferencePage(3), new ReferencePage(0), new ReferencePage(4)
        );
    }

    private void onRunSimulation() {

        if (referenceList.isEmpty()) {
            showError("Please add at least one page reference before running the simulation.");
            return;
        }

        for (ReferencePage p : referenceList) {
            if (p.getPageNumber() < 0) {
                showError("Page numbers cannot be negative.");
                return;
            }
        }

        int frameCount;
        try {
            frameCount = Integer.parseInt(frameCountField.getText().trim());
            if (frameCount <= 0) {
                showError("Frame count must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Frame count must be a valid whole number.");
            return;
        }

        List<Integer> referenceString = referenceList.stream()
                .map(ReferencePage::getPageNumber)
                .collect(Collectors.toList());

        String selectedAlgorithm = algorithmSelector.getValue();
        PageReplacementAlgorithm algorithm = createAlgorithm(selectedAlgorithm);

        if (algorithm == null) {
            return;
        }

        PageReplacementResult result = algorithm.run(referenceString, frameCount);
        displayResults(result);
    }

    private PageReplacementAlgorithm createAlgorithm(String name) {
        switch (name) {
            case "FIFO":
                return new FIFO();
            case "LRU":
                return new LRU();
            case "Optimal":
                return new Optimal();
            default:
                return null;
        }
    }

    private void displayResults(PageReplacementResult result) {

        hitCountLabel.setText(String.valueOf(result.getHitCount()));
        faultCountLabel.setText(String.valueOf(result.getFaultCount()));
        hitRatioLabel.setText(String.format("%.2f", result.getHitRatio()));
        faultRatioLabel.setText(String.format("%.2f", result.getFaultRatio()));
        metricsSection.setVisible(true);
        metricsSection.setManaged(true);

        timelineContainer.getChildren().clear();
        timelineContainer.getChildren().add(PageReplacementVisualizer.render(result.getSteps()));
        timelineSection.setVisible(true);
        timelineSection.setManaged(true);

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

        private void onClearAll() {
        referenceList.clear();
        metricsSection.setVisible(false);
        metricsSection.setManaged(false);
        timelineSection.setVisible(false);
        timelineSection.setManaged(false);
        resultsSection.setVisible(false);
        resultsSection.setManaged(false);
    }
}