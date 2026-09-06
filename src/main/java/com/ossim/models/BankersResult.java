package com.ossim.models;

import java.util.List;

public class BankersResult {

    private boolean safe;
    private List<Integer> safeSequence;
    private List<BankersStepResult> steps;
    private List<List<Integer>> needMatrix;

    public BankersResult(boolean safe,
                          List<Integer> safeSequence,
                          List<BankersStepResult> steps,
                          List<List<Integer>> needMatrix) {
        this.safe = safe;
        this.safeSequence = safeSequence;
        this.steps = steps;
        this.needMatrix = needMatrix;
    }

    public boolean isSafe() {
        return safe;
    }

    public List<Integer> getSafeSequence() {
        return safeSequence;
    }

    public List<BankersStepResult> getSteps() {
        return steps;
    }

    public List<List<Integer>> getNeedMatrix() {
        return needMatrix;
    }
}