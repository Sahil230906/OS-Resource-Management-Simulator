package com.ossim.visualization;

import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.List;

public class AnimationUtil {

    private static final Duration BLOCK_REVEAL_DURATION = Duration.millis(220);
    private static final Duration STAGGER_GAP = Duration.millis(90);

    /**
     * Plays a one-time reveal animation over a list of nodes, appearing
     * one after another in order. Purely cosmetic — every node is already
     * fully built with correct final content before this runs, so the
     * eventual static result is identical whether the animation plays,
     * gets skipped, or the app freezes mid-play. Call this AFTER adding
     * the nodes to their container, since it only manipulates their
     * scale, not their presence in the scene graph.
     */
    public static void revealSequentially(List<? extends Node> nodes) {

        SequentialTransition sequence = new SequentialTransition();

        for (Node node : nodes) {
            node.setScaleX(0);
            node.setScaleY(0);

            ScaleTransition scaleIn = new ScaleTransition(BLOCK_REVEAL_DURATION, node);
            scaleIn.setToX(1);
            scaleIn.setToY(1);

            PauseTransition stagger = new PauseTransition(STAGGER_GAP);

            sequence.getChildren().addAll(scaleIn, stagger);
        }

        sequence.play();
    }
}