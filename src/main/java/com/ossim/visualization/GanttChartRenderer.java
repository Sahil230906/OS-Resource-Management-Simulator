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

    private static final String[] PALETTE = {
            "#89b4fa", "#f38ba8", "#a6e3a1", "#fab387",
            "#cba6f7", "#f9e2af", "#94e2d5", "#eba0ac"
    };

    private static final double PIXELS_PER_UNIT_TIME = 30;
    private static final double BLOCK_HEIGHT = 50;

    public static Pane render(List<GanttEntry> entries) {

        VBox container = new VBox(4);

        HBox blockRow = new HBox(0);
        HBox labelRow = new HBox(0);

        Map<String, String> colorAssignment = new LinkedHashMap<>();

        for (GanttEntry entry : entries) {

            String color = colorAssignment.computeIfAbsent(entry.getProcessId(), id -> {
                return PALETTE[colorAssignment.size() % PALETTE.length];
            });

            double width = entry.getDuration() * PIXELS_PER_UNIT_TIME;

            Rectangle rect = new Rectangle(width, BLOCK_HEIGHT);
            rect.setFill(Color.web(color));
            rect.setArcWidth(6);
            rect.setArcHeight(6);

            Text label = new Text(entry.getProcessId());
            label.setFont(Font.font(13));
            label.setStyle("-fx-fill: #1e1e2e; -fx-font-weight: bold;");

            StackPane block = new StackPane(rect, label);
            blockRow.getChildren().add(block);

            Text startLabel = new Text(String.valueOf(entry.getStartTime()));
            startLabel.setStyle("-fx-fill: #a6adc8; -fx-font-size: 11px;");
            StackPane timeMarker = new StackPane(startLabel);
            timeMarker.setPrefWidth(width);
            timeMarker.setPadding(new Insets(2, 0, 0, 0));
            labelRow.getChildren().add(timeMarker);
        }

        if (!entries.isEmpty()) {
            GanttEntry last = entries.get(entries.size() - 1);
            Text endLabel = new Text(String.valueOf(last.getEndTime()));
            endLabel.setStyle("-fx-fill: #a6adc8; -fx-font-size: 11px;");
            labelRow.getChildren().add(endLabel);
        }

        container.getChildren().addAll(blockRow, labelRow);

        AnimationUtil.revealSequentially(blockRow.getChildren());

        return container;
    }
}