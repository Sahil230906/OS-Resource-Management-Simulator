package com.ossim.visualization;

import com.ossim.models.PageStepResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;

public class PageReplacementVisualizer {

    private static final double SLOT_WIDTH = 70;
    private static final double SLOT_HEIGHT = 36;
    private static final double COLUMN_SPACING = 4;
    private static final double STEP_SPACING = 14;

    private static final String FILLED_COLOR = "#89b4fa";
    private static final String EMPTY_COLOR = "#45475a";
    private static final String HIT_COLOR = "#a6e3a1";
    private static final String FAULT_COLOR = "#f38ba8";

    /**
     * Builds a horizontal step-by-step timeline from a full algorithm run.
     * Every step is visible at once — correctness must be provable without
     * clicking through anything. Nothing here is hardcoded to any specific
     * algorithm or reference string; entirely driven by the PageStepResult list.
     */
    public static Pane render(List<PageStepResult> steps) {

        HBox container = new HBox(STEP_SPACING);

        for (PageStepResult step : steps) {

            VBox column = new VBox(COLUMN_SPACING);
            column.setAlignment(Pos.TOP_CENTER);
            column.setPadding(new Insets(8));

            String outcomeColor = step.isHit() ? HIT_COLOR : FAULT_COLOR;
            column.setStyle("-fx-border-color: " + outcomeColor +
                    "; -fx-border-width: 2; -fx-border-radius: 6; -fx-background-radius: 6;");

            Text pageLabel = new Text("Ref: " + step.getPageRequested());
            pageLabel.setStyle("-fx-fill: #cdd6f4; -fx-font-weight: bold; -fx-font-size: 12px;");
            column.getChildren().add(pageLabel);

            // One rectangle per frame slot, in order — empty slots show a dash
            for (Integer framePage : step.getFrameState()) {
                boolean filled = framePage != null;

                Rectangle rect = new Rectangle(SLOT_WIDTH, SLOT_HEIGHT);
                rect.setFill(Color.web(filled ? FILLED_COLOR : EMPTY_COLOR));
                rect.setArcWidth(6);
                rect.setArcHeight(6);

                Text slotLabel = new Text(filled ? String.valueOf(framePage) : "-");
                slotLabel.setStyle("-fx-fill: #1e1e2e; -fx-font-weight: bold;");

                StackPane slot = new StackPane(rect, slotLabel);
                column.getChildren().add(slot);
            }

            Text outcomeLabel = new Text(step.isHit() ? "HIT" : "FAULT");
            outcomeLabel.setStyle("-fx-fill: " + outcomeColor + "; -fx-font-weight: bold; -fx-font-size: 11px;");
            column.getChildren().add(outcomeLabel);

            if (step.getEvictedPage() != null) {
                Text evictedLabel = new Text("Evicted " + step.getEvictedPage());
                evictedLabel.setStyle("-fx-fill: #f9e2af; -fx-font-size: 10px;");
                column.getChildren().add(evictedLabel);
            }

            container.getChildren().add(column);
        }

        return container;
    }
}