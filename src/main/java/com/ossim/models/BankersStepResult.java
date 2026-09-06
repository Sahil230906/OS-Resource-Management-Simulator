package com.ossim.models;

import java.util.List;

public class BankersStepResult {

    private int processIndex;
    private List<Integer> workBefore;
    private List<Integer> workAfter;

    public BankersStepResult(int processIndex, List<Integer> workBefore, List<Integer> workAfter) {
        this.processIndex = processIndex;
        this.workBefore = workBefore;
        this.workAfter = workAfter;
    }

    public int getProcessIndex() {
        return processIndex;
    }

    public List<Integer> getWorkBefore() {
        return workBefore;
    }

    public List<Integer> getWorkAfter() {
        return workAfter;
    }

    @Override
    public String toString() {
        return "BankersStepResult{" +
                "process=P" + processIndex +
                ", workBefore=" + workBefore +
                ", workAfter=" + workAfter +
                '}';
    }
}