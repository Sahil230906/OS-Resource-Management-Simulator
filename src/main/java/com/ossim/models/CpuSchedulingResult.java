package com.ossim.models;

import java.util.List;

public class CpuSchedulingResult {

    private List<ProcessResult> processResults;
    private List<GanttEntry> ganttEntries;
    private double averageWaitingTime;
    private double averageTurnaroundTime;
    private double averageResponseTime;

    public CpuSchedulingResult(List<ProcessResult> processResults,
                                List<GanttEntry> ganttEntries,
                                double averageWaitingTime,
                                double averageTurnaroundTime,
                                double averageResponseTime) {
        this.processResults = processResults;
        this.ganttEntries = ganttEntries;
        this.averageWaitingTime = averageWaitingTime;
        this.averageTurnaroundTime = averageTurnaroundTime;
        this.averageResponseTime = averageResponseTime;
    }

    public List<ProcessResult> getProcessResults() {
        return processResults;
    }

    public List<GanttEntry> getGanttEntries() {
        return ganttEntries;
    }

    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public double getAverageTurnaroundTime() {
        return averageTurnaroundTime;
    }

    public double getAverageResponseTime() {
        return averageResponseTime;
    }
}