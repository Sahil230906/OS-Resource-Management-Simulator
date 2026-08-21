package com.ossim.algorithms.cpu;

import com.ossim.models.CpuSchedulingResult;
import com.ossim.models.GanttEntry;
import com.ossim.models.Process;
import com.ossim.models.ProcessResult;

import java.util.ArrayList;
import java.util.List;

public class PriorityScheduling implements CpuSchedulingAlgorithm {

    @Override
    public CpuSchedulingResult run(List<Process> processes) {

        List<Process> remaining = new ArrayList<>(processes);
        List<ProcessResult> processResults = new ArrayList<>();
        List<GanttEntry> ganttEntries = new ArrayList<>();

        int currentTime = 0;
        int totalWaiting = 0;
        int totalTurnaround = 0;
        int totalResponse = 0;
        int count = processes.size();

        while (!remaining.isEmpty()) {

            // Step 1: Among arrived processes, find the one with highest priority
            // (lower number = higher priority, standard convention)
            Process next = null;
            for (Process p : remaining) {
                if (p.getArrivalTime() <= currentTime) {
                    if (next == null || p.getPriority() < next.getPriority()) {
                        next = p;
                    }
                }
            }

            // Step 2: If nobody has arrived yet, jump time forward to the earliest arrival
            if (next == null) {
                int earliestArrival = Integer.MAX_VALUE;
                for (Process p : remaining) {
                    earliestArrival = Math.min(earliestArrival, p.getArrivalTime());
                }
                currentTime = earliestArrival;
                continue;
            }

            // Step 3: Run the selected process (non-preemptive: runs to completion)
            int startTime = currentTime;
            int completionTime = startTime + next.getBurstTime();

            int turnaroundTime = completionTime - next.getArrivalTime();
            int waitingTime = turnaroundTime - next.getBurstTime();
            int responseTime = startTime - next.getArrivalTime();

            processResults.add(new ProcessResult(
                    next.getProcessId(),
                    next.getArrivalTime(),
                    next.getBurstTime(),
                    completionTime,
                    turnaroundTime,
                    waitingTime,
                    responseTime
            ));

            ganttEntries.add(new GanttEntry(next.getProcessId(), startTime, completionTime));

            totalWaiting += waitingTime;
            totalTurnaround += turnaroundTime;
            totalResponse += responseTime;

            currentTime = completionTime;
            remaining.remove(next);
        }

        double avgWaiting = (double) totalWaiting / count;
        double avgTurnaround = (double) totalTurnaround / count;
        double avgResponse = (double) totalResponse / count;

        return new CpuSchedulingResult(processResults, ganttEntries, avgWaiting, avgTurnaround, avgResponse);
    }

    @Override
    public String getName() {
        return "Priority Scheduling";
    }
}