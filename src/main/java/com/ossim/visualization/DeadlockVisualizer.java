package com.ossim.visualization;

import com.ossim.models.BankersStepResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;

public class DeadlockVisualizer {

    /**
     * Renders the Allocation, Max, and Need matrices side by side as
     * labeled grids. Entirely driven by the matrices passed in — row/column
     * counts come from the data, nothing hardcoded to a specific process
     * or resource count.
     */
    public static Pane renderMatrices(int numProcesses, int numResources,
                                       List<List<Integer>> allocation,
                                       List<List<Integer>> max,
                                       List<List<Integer>> need) {

        HBox container = new HBox(30);
        container.getChildren().add(buildMatrixCard("Allocation", numProcesses, numResources, allocation));
        container.getChildren().add(buildMatrixCard("Max", numProcesses, numResources, max));
        container.getChildren().add(buildMatrixCard("Need", numProcesses, numResources, need));
        return container;
    }

    private static VBox buildMatrixCard(String title, int numProcesses, int numResources, List<List<Integer>> matrix) {

        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-border-color: #45475a; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");

        Text titleText = new Text(title);
        titleText.setStyle("-fx-fill: #cdd6f4; -fx-font-weight: bold; -fx-font-size: 13px;");
        card.getChildren().add(titleText);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(6);

        for (int j = 0; j < numResources; j++) {
            Text header = new Text("R" + j);
            header.setStyle("-fx-fill: #a6adc8; -fx-font-size: 11px; -fx-font-weight: bold;");
            grid.add(header, j + 1, 0);
        }

        for (int i = 0; i < numProcesses; i++) {
            Text rowLabel = new Text("P" + i);
            rowLabel.setStyle("-fx-fill: #a6adc8; -fx-font-size: 11px; -fx-font-weight: bold;");
            grid.add(rowLabel, 0, i + 1);

            for (int j = 0; j < numResources; j++) {
                Text cell = new Text(String.valueOf(matrix.get(i).get(j)));
                cell.setStyle("-fx-fill: #cdd6f4; -fx-font-size: 12px;");
                grid.add(cell, j + 1, i + 1);
            }
        }

        card.getChildren().add(grid);
        return card;
    }

    /**
     * Renders the safe-sequence trace: the starting Work vector, then one
     * box per process that finished, each showing the Work vector after it
     * released its resources. If the run was unsafe, steps will be empty
     * and only the starting box is shown — the controller is responsible
     * for surfacing the "unsafe" verdict as text separately.
     */
    public static Pane renderSafeSequence(List<BankersStepResult> steps, List<Integer> available) {

        HBox container = new HBox(14);
        container.getChildren().add(buildWorkBox("Start", available));

        for (BankersStepResult step : steps) {
            container.getChildren().add(buildStepBox(step));
        }

        return container;
    }

    private static VBox buildWorkBox(String label, List<Integer> work) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-border-color: #6c7086; -fx-border-width: 2; -fx-border-radius: 6; -fx-background-radius: 6;");

        Text labelText = new Text(label);
        labelText.setStyle("-fx-fill: #cdd6f4; -fx-font-weight: bold; -fx-font-size: 12px;");

        Text workText = new Text(work.toString());
        workText.setStyle("-fx-fill: #a6e3a1; -fx-font-size: 12px;");

        box.getChildren().addAll(labelText, workText);
        return box;
    }

    private static VBox buildStepBox(BankersStepResult step) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-border-color: #a6e3a1; -fx-border-width: 2; -fx-border-radius: 6; -fx-background-radius: 6;");

        Text procLabel = new Text("P" + step.getProcessIndex() + " finishes");
        procLabel.setStyle("-fx-fill: #cdd6f4; -fx-font-weight: bold; -fx-font-size: 12px;");

        Text workText = new Text("Work → " + step.getWorkAfter());
        workText.setStyle("-fx-fill: #a6e3a1; -fx-font-size: 12px;");

        box.getChildren().addAll(procLabel, workText);
        return box;
    }
}