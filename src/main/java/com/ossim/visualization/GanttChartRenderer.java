package com.ossim.visualization;

import com.ossim.models.GanttEntry;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GanttChartRenderer {

    // A fixed palette the renderer cycles through — colors are assigned to
    // process IDs dynamically as they're first encountered, never hardcoded per process.
    private static final String[] PALETTE = {
            "#89b4fa", "#f38ba8", "#a6e3a1", "#fab387",
            "#cba6f7", "#f9e2af", "#94e2d5", "#eba0ac"
    };

    private static final double PIXELS_PER_UNIT_TIME = 30;
    private static final double BLOCK_HEIGHT = 50;

    /**
     * Builds a Gantt chart visual from a list of execution segments.
     * Nothing here is hardcoded to any specific algorithm or process —
     * the chart is entirely driven by whatever GanttEntry list is passed in.
     */
    public static Pane render(List<GanttEntry> entries) {

        VBox container = new VBox(4);

        HBox blockRow = new HBox(0);
        HBox labelRow = new HBox(0);

        Map<String, String> colorAssignment = new LinkedHashMap<>();
        // int colorIndex = 0;

        for (GanttEntry entry : entries) {

            // Assign a color to this process ID the first time we see it
            String color = colorAssignment.computeIfAbsent(entry.getProcessId(), id -> {
                return PALETTE[colorAssignment.size() % PALETTE.length];
            });

            double width = entry.getDuration() * PIXELS_PER_UNIT_TIME;

            // The block itself
            Rectangle rect = new Rectangle(width, BLOCK_HEIGHT);
            rect.setFill(Color.web(color));
            rect.setArcWidth(6);
            rect.setArcHeight(6);

            Text label = new Text(entry.getProcessId());
            label.setFont(Font.font(13));
            label.setStyle("-fx-fill: #1e1e2e; -fx-font-weight: bold;");

            StackPane block = new StackPane(rect, label);
            blockRow.getChildren().add(block);

            // Time markers underneath: start time, then end time for the last block
            Text startLabel = new Text(String.valueOf(entry.getStartTime()));
            startLabel.setStyle("-fx-fill: #a6adc8; -fx-font-size: 11px;");
            StackPane timeMarker = new StackPane(startLabel);
            timeMarker.setPrefWidth(width);
            timeMarker.setPadding(new Insets(2, 0, 0, 0));
            labelRow.getChildren().add(timeMarker);
        }

        // Add the final end-time label after the last block
        if (!entries.isEmpty()) {
            GanttEntry last = entries.get(entries.size() - 1);
            Text endLabel = new Text(String.valueOf(last.getEndTime()));
            endLabel.setStyle("-fx-fill: #a6adc8; -fx-font-size: 11px;");
            labelRow.getChildren().add(endLabel);
        }

        container.getChildren().addAll(blockRow, labelRow);
        return container;
    }
}