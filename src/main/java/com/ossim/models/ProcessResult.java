package com.ossim.models;

public class ProcessResult {

    private String processId;
    private int arrivalTime;
    private int burstTime;
    private int completionTime;
    private int turnaroundTime;
    private int waitingTime;
    private int responseTime;

    public ProcessResult(String processId, int arrivalTime, int burstTime,
                          int completionTime, int turnaroundTime,
                          int waitingTime, int responseTime) {
        this.processId = processId;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.completionTime = completionTime;
        this.turnaroundTime = turnaroundTime;
        this.waitingTime = waitingTime;
        this.responseTime = responseTime;
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

    public int getCompletionTime() {
        return completionTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getResponseTime() {
        return responseTime;
    }

    @Override
    public String toString() {
        return "ProcessResult{" +
                "id='" + processId + '\'' +
                ", completion=" + completionTime +
                ", turnaround=" + turnaroundTime +
                ", waiting=" + waitingTime +
                ", response=" + responseTime +
                '}';
    }
}