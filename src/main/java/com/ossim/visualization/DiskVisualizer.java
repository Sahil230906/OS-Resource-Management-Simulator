package com.ossim.visualization;

import com.ossim.models.DiskStepResult;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.List;

public class DiskVisualizer {

    private static final double GRAPH_WIDTH = 700;
    private static final double STEP_HEIGHT = 42;
    private static final double TOP_MARGIN = 40;
    private static final double LEFT_MARGIN = 30;

    private static final String REQUEST_COLOR = "#89b4fa";
    private static final String BOUNDARY_COLOR = "#585b70";
    private static final String POINT_COLOR = "#f9e2af";
    private static final String AXIS_COLOR = "#6c7086";

    /**
     * Builds a seek-time graph: track number across the top (0 to diskSize),
     * time flowing downward, with a zigzag line tracing the head's actual
     * path. Boundary/wrap segments (SCAN/C-SCAN touching an end with no real
     * request there) are drawn dashed and grey to distinguish them from
     * genuine request stops. Entirely driven by the step list — nothing
     * hardcoded to any specific algorithm.
     */
    public static Pane render(List<DiskStepResult> steps, int diskSize) {

        Pane pane = new Pane();

        double totalHeight = TOP_MARGIN + (steps.size() + 1) * STEP_HEIGHT;
        pane.setPrefSize(LEFT_MARGIN * 2 + GRAPH_WIDTH, totalHeight);

        // ===== Axis line + tick labels =====
        Line axis = new Line(LEFT_MARGIN, TOP_MARGIN, LEFT_MARGIN + GRAPH_WIDTH, TOP_MARGIN);
        axis.setStroke(Color.web(AXIS_COLOR));
        pane.getChildren().add(axis);

        int[] ticks = {0, diskSize / 4, diskSize / 2, (diskSize * 3) / 4, diskSize};
        for (int t : ticks) {
            double x = trackToX(t, diskSize);

            Line tick = new Line(x, TOP_MARGIN - 5, x, TOP_MARGIN + 5);
            tick.setStroke(Color.web(AXIS_COLOR));
            pane.getChildren().add(tick);

            Text tickLabel = new Text(String.valueOf(t));
            tickLabel.setStyle("-fx-fill: #a6adc8; -fx-font-size: 10px;");
            tickLabel.setX(x - 10);
            tickLabel.setY(TOP_MARGIN - 12);
            pane.getChildren().add(tickLabel);
        }

        if (steps.isEmpty()) {
            return pane;
        }

        // ===== Starting head position =====
        double prevX = trackToX(steps.get(0).getFromTrack(), diskSize);
        double prevY = TOP_MARGIN;
        addPoint(pane, prevX, prevY, steps.get(0).getFromTrack(), POINT_COLOR);

        // ===== One zigzag segment per step, moving down the page over time =====
        for (int i = 0; i < steps.size(); i++) {
            DiskStepResult step = steps.get(i);

            double x = trackToX(step.getToTrack(), diskSize);
            double y = TOP_MARGIN + (i + 1) * STEP_HEIGHT;

            Line segment = new Line(prevX, prevY, x, y);
            segment.setStrokeWidth(2);

            if (step.isBoundaryMove()) {
                segment.setStroke(Color.web(BOUNDARY_COLOR));
                segment.getStrokeDashArray().addAll(6.0, 4.0);
                addPoint(pane, x, y, step.getToTrack(), BOUNDARY_COLOR);
            } else {
                segment.setStroke(Color.web(REQUEST_COLOR));
                addPoint(pane, x, y, step.getToTrack(), POINT_COLOR);
            }

            pane.getChildren().add(segment);

            prevX = x;
            prevY = y;
        }

        return pane;
    }

    private static double trackToX(int track, int diskSize) {
        return LEFT_MARGIN + (track / (double) diskSize) * GRAPH_WIDTH;
    }

    private static void addPoint(Pane pane, double x, double y, int trackValue, String color) {
        Circle dot = new Circle(x, y, 4);
        dot.setFill(Color.web(color));
        pane.getChildren().add(dot);

        Text label = new Text(String.valueOf(trackValue));
        label.setStyle("-fx-fill: #cdd6f4; -fx-font-size: 10px;");
        label.setX(x + 7);
        label.setY(y + 4);
        pane.getChildren().add(label);
    }
}