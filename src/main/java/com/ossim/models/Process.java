package com.ossim.models;

public class Process {

    private String processId;
    private int arrivalTime;
    private int burstTime;
    private int priority;

    // Used internally by algorithms during simulation (e.g. Round Robin)
    private int remainingTime;

    public Process(String processId, int arrivalTime, int burstTime, int priority) {
        this.processId = processId;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
    }

    // ===== Getters =====
    public String getProcessId() {
        return processId;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getPriority() {
        return priority;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    // ===== Setters (needed for editable process lists in the UI) =====
    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    @Override
    public String toString() {
        return "Process{" +
                "id='" + processId + '\'' +
                ", arrival=" + arrivalTime +
                ", burst=" + burstTime +
                ", priority=" + priority +
                '}';
    }
}