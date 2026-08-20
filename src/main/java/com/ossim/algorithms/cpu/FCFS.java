package com.ossim.algorithms.cpu;

import com.ossim.models.CpuSchedulingResult;
import com.ossim.models.GanttEntry;
import com.ossim.models.Process;
import com.ossim.models.ProcessResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FCFS implements CpuSchedulingAlgorithm {

    @Override
    public CpuSchedulingResult run(List<Process> processes) {

        // Step 1: Sort processes by arrival time (FCFS = whoever arrives first, runs first)
        List<Process> sorted = new ArrayList<>(processes);
        sorted.sort(Comparator.comparingInt(Process::getArrivalTime));

        List<ProcessResult> processResults = new ArrayList<>();
        List<GanttEntry> ganttEntries = new ArrayList<>();

        int currentTime = 0;
        int totalWaiting = 0;
        int totalTurnaround = 0;
        int totalResponse = 0;

        for (Process p : sorted) {

            // If the CPU is idle waiting for this process to arrive, jump time forward
            if (currentTime < p.getArrivalTime()) {
                currentTime = p.getArrivalTime();
            }

            int startTime = currentTime;
            int completionTime = startTime + p.getBurstTime();

            int turnaroundTime = completionTime - p.getArrivalTime();
            int waitingTime = turnaroundTime - p.getBurstTime();
            int responseTime = startTime - p.getArrivalTime(); // FCFS: process runs once, no preemption

            processResults.add(new ProcessResult(
                    p.getProcessId(),
                    p.getArrivalTime(),
                    p.getBurstTime(),
                    completionTime,
                    turnaroundTime,
                    waitingTime,
                    responseTime
            ));

            ganttEntries.add(new GanttEntry(p.getProcessId(), startTime, completionTime));

            totalWaiting += waitingTime;
            totalTurnaround += turnaroundTime;
            totalResponse += responseTime;

            currentTime = completionTime;
        }

        int count = processes.size();
        double avgWaiting = (double) totalWaiting / count;
        double avgTurnaround = (double) totalTurnaround / count;
        double avgResponse = (double) totalResponse / count;

        return new CpuSchedulingResult(processResults, ganttEntries, avgWaiting, avgTurnaround, avgResponse);
    }

    @Override
    public String getName() {
        return "FCFS";
    }
}