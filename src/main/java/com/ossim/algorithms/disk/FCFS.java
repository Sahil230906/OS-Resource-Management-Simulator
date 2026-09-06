package com.ossim.algorithms.disk;

import com.ossim.models.DiskSchedulingResult;
import com.ossim.models.DiskStepResult;

import java.util.ArrayList;
import java.util.List;

public class FCFS implements DiskSchedulingAlgorithm {

    @Override
    public DiskSchedulingResult run(int initialHead, List<Integer> requests, int diskSize, boolean towardZero) {

        List<DiskStepResult> steps = new ArrayList<>();
        int currentHead = initialHead;
        int totalMovement = 0;

        // Services requests strictly in the order given — no reordering at all
        for (int request : requests) {
            int movement = Math.abs(currentHead - request);
            steps.add(new DiskStepResult(currentHead, request, movement));
            totalMovement += movement;
            currentHead = request;
        }

        double averageMovement = requests.isEmpty() ? 0.0 : (double) totalMovement / requests.size();

        return new DiskSchedulingResult(steps, totalMovement, averageMovement);
    }

    @Override
    public String getName() {
        return "FCFS";
    }
}