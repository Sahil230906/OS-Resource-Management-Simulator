package com.ossim.visualization;

import com.ossim.models.MemoryBlock;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MemoryVisualizer {

    // Same idea as GanttChartRenderer's PALETTE — colors are assigned to
    // process IDs dynamically as they're first encountered, never hardcoded.
    private static final String[] PALETTE = {
            "#89b4fa", "#f38ba8", "#a6e3a1", "#fab387",
            "#cba6f7", "#f9e2af", "#94e2d5", "#eba0ac"
    };

    // Free blocks always get this neutral color, since they belong to no process.
    private static final String FREE_COLOR = "#45475a";

    private static final double BLOCK_WIDTH = 260;
    private static final double PIXELS_PER_UNIT_SIZE = 0.3;
    private static final double MIN_BLOCK_HEIGHT = 40;

    /**
     * Builds a vertical memory map from the final block state after an
     * allocation run. Nothing here is hardcoded to any specific algorithm,
     * process, or partition layout — entirely driven by the MemoryBlock
     * list passed in.
     */
    public static Pane render(List<MemoryBlock> blocks) {

        VBox container = new VBox(2);

        Map<String, String> colorAssignment = new LinkedHashMap<>();

        for (MemoryBlock block : blocks) {

            double height = Math.max(MIN_BLOCK_HEIGHT, block.getSize() * PIXELS_PER_UNIT_SIZE);

            String color;
            String labelText;

            if (block.isAllocated()) {
                color = colorAssignment.computeIfAbsent(block.getAllocatedProcessId(), id ->
                        PALETTE[colorAssignment.size() % PALETTE.length]);
                labelText = block.getAllocatedProcessId() + " - " + block.getAllocatedProcessSize() + " MB\nALLOCATED";
            } else {
                color = FREE_COLOR;
                labelText = "FREE - " + block.getSize() + " MB";
            }

            Rectangle rect = new Rectangle(BLOCK_WIDTH, height);
            rect.setFill(Color.web(color));
            rect.setArcWidth(8);
            rect.setArcHeight(8);
            rect.setStroke(Color.web("#1e1e2e"));
            rect.setStrokeWidth(1.5);

            Text label = new Text(labelText);
            label.setFont(Font.font(13));
            label.setTextAlignment(TextAlignment.CENTER);
            label.setStyle("-fx-fill: #1e1e2e; -fx-font-weight: bold;");

            StackPane blockPane = new StackPane(rect, label);
            container.getChildren().add(blockPane);
        }

        return container;
    }
}