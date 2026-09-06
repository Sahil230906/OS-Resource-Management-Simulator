package com.ossim.algorithms.disk;

import com.ossim.models.DiskSchedulingResult;
import com.ossim.models.DiskStepResult;

import java.util.ArrayList;
import java.util.List;

public class SSTF implements DiskSchedulingAlgorithm {

    @Override
    public DiskSchedulingResult run(int initialHead, List<Integer> requests, int diskSize, boolean towardZero) {

        List<Integer> remaining = new ArrayList<>(requests);
        List<DiskStepResult> steps = new ArrayList<>();

        int currentHead = initialHead;
        int totalMovement = 0;

        // At each step, service whichever remaining request is closest
        // to the current head — no fixed order at all
        while (!remaining.isEmpty()) {
            int nearestIndex = findNearestIndex(remaining, currentHead);
            int target = remaining.remove(nearestIndex);

            int movement = Math.abs(currentHead - target);
            steps.add(new DiskStepResult(currentHead, target, movement));
            totalMovement += movement;
            currentHead = target;
        }

        double averageMovement = requests.isEmpty() ? 0.0 : (double) totalMovement / requests.size();

        return new DiskSchedulingResult(steps, totalMovement, averageMovement);
    }

    /**
     * Finds the index of whichever remaining request is closest to the
     * current head. Ties go to whichever appears first in the list.
     */
    private int findNearestIndex(List<Integer> remaining, int currentHead) {
        int nearestIndex = 0;
        int minDistance = Math.abs(remaining.get(0) - currentHead);

        for (int i = 1; i < remaining.size(); i++) {
            int distance = Math.abs(remaining.get(i) - currentHead);
            if (distance < minDistance) {
                minDistance = distance;
                nearestIndex = i;
            }
        }

        return nearestIndex;
    }

    @Override
    public String getName() {
        return "SSTF";
    }
}