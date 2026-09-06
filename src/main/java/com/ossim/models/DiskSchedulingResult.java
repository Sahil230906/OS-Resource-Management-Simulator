package com.ossim.models;

import java.util.List;

public class DiskSchedulingResult {

    private List<DiskStepResult> steps;
    private int totalHeadMovement;
    private double averageHeadMovement;

    public DiskSchedulingResult(List<DiskStepResult> steps,
                                 int totalHeadMovement,
                                 double averageHeadMovement) {
        this.steps = steps;
        this.totalHeadMovement = totalHeadMovement;
        this.averageHeadMovement = averageHeadMovement;
    }

    public List<DiskStepResult> getSteps() {
        return steps;
    }

    public int getTotalHeadMovement() {
        return totalHeadMovement;
    }

    public double getAverageHeadMovement() {
        return averageHeadMovement;
    }
}