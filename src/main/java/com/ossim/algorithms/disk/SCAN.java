package com.ossim.algorithms.disk;

import com.ossim.models.DiskSchedulingResult;
import com.ossim.models.DiskStepResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SCAN implements DiskSchedulingAlgorithm {

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
                // Request exactly at the starting head — zero movement to "reach" it
                steps.add(new DiskStepResult(currentHead, r, 0));
            }
        }

        Collections.sort(lower);
        Collections.sort(upper);

        if (towardZero) {
            // Phase 1: sweep down, closest-to-head first (descending)
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

            // Phase 2: sweep up (ascending)
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

        } else {
            // Mirror image: sweep up first, then down
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
        }

        double averageMovement = requests.isEmpty() ? 0.0 : (double) totalMovement / requests.size();

        return new DiskSchedulingResult(steps, totalMovement, averageMovement);
    }

    @Override
    public String getName() {
        return "SCAN";
    }
}