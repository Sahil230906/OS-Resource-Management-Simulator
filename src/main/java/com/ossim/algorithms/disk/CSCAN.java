package com.ossim.algorithms.disk;

import com.ossim.models.DiskSchedulingResult;
import com.ossim.models.DiskStepResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CSCAN implements DiskSchedulingAlgorithm {

    @Override
    public DiskSchedulingResult run(int initialHead, List<Integer> requests, int diskSize, boolean towardZero) {

        List<DiskStepResult> steps = new ArrayList<>();
        int currentHead = initialHead;
        int totalMovement = 0;

        List<Integer> lower = new ArrayList<>();
        List<Integer> upper = new ArrayList<>();
        for (int r : requests) {
            if (r < initialHead) {
                lower.add(r);
            } else if (r > initialHead) {
                upper.add(r);
            } else {
                steps.add(new DiskStepResult(currentHead, r, 0));
            }
        }

        Collections.sort(lower);
        Collections.sort(upper);

        if (towardZero) {
            // Phase 1: decreasing through lower requests
            for (int i = lower.size() - 1; i >= 0; i--) {
                int target = lower.get(i);
                int movement = Math.abs(currentHead - target);
                steps.add(new DiskStepResult(currentHead, target, movement));
                totalMovement += movement;
                currentHead = target;
            }
            if (currentHead != 0) {
                int movement = currentHead;
                steps.add(new DiskStepResult(currentHead, 0, movement, true));
                totalMovement += movement;
                currentHead = 0;
            }

            // Wrap to the far end — only if there's more to service that side
            if (!upper.isEmpty()) {
                int movement = diskSize;
                steps.add(new DiskStepResult(currentHead, diskSize, movement, true));
                totalMovement += movement;
                currentHead = diskSize;

                // Phase 2: still decreasing, now from the far end downward
                for (int i = upper.size() - 1; i >= 0; i--) {
                    int target = upper.get(i);
                    int movement2 = Math.abs(currentHead - target);
                    steps.add(new DiskStepResult(currentHead, target, movement2));
                    totalMovement += movement2;
                    currentHead = target;
                }
            }

        } else {
            // Mirror image: fixed direction is increasing
            for (int target : upper) {
                int movement = Math.abs(currentHead - target);
                steps.add(new DiskStepResult(currentHead, target, movement));
                totalMovement += movement;
                currentHead = target;
            }
            if (currentHead != diskSize) {
                int movement = diskSize - currentHead;
                steps.add(new DiskStepResult(currentHead, diskSize, movement, true));
                totalMovement += movement;
                currentHead = diskSize;
            }

            if (!lower.isEmpty()) {
                int movement = diskSize;
                steps.add(new DiskStepResult(currentHead, 0, movement, true));
                totalMovement += movement;
                currentHead = 0;

                for (int target : lower) {
                    int movement2 = Math.abs(currentHead - target);
                    steps.add(new DiskStepResult(currentHead, target, movement2));
                    totalMovement += movement2;
                    currentHead = target;
                }
            }
        }

        double averageMovement = requests.isEmpty() ? 0.0 : (double) totalMovement / requests.size();

        return new DiskSchedulingResult(steps, totalMovement, averageMovement);
    }

    @Override
    public String getName() {
        return "C-SCAN";
    }
}