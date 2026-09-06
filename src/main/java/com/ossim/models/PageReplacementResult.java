package com.ossim.models;

import java.util.List;

public class PageReplacementResult {

    private List<PageStepResult> steps;
    private int hitCount;
    private int faultCount;
    private double hitRatio;
    private double faultRatio;

    public PageReplacementResult(List<PageStepResult> steps,
                                  int hitCount,
                                  int faultCount,
                                  double hitRatio,
                                  double faultRatio) {
        this.steps = steps;
        this.hitCount = hitCount;
        this.faultCount = faultCount;
        this.hitRatio = hitRatio;
        this.faultRatio = faultRatio;
    }

    public List<PageStepResult> getSteps() {
        return steps;
    }

    public int getHitCount() {
        return hitCount;
    }

    public int getFaultCount() {
        return faultCount;
    }

    public double getHitRatio() {
        return hitRatio;
    }

    public double getFaultRatio() {
        return faultRatio;
    }
}