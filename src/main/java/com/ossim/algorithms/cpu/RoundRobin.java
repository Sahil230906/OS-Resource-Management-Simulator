package com.ossim.algorithms.cpu;

import com.ossim.models.CpuSchedulingResult;
import com.ossim.models.GanttEntry;
import com.ossim.models.Process;
import com.ossim.models.ProcessResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class RoundRobin implements CpuSchedulingAlgorithm {

    private final int timeQuantum;

    public RoundRobin(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }

    @Override
    public CpuSchedulingResult run(List<Process> processes) {

        // Work on copies so we don't mutate the caller's original Process objects
        List<Process> all = new ArrayList<>();
        for (Process p : processes) {
            Process copy = new Process(p.getProcessId(), p.getArrivalTime(), p.getBurstTime(), p.getPriority());
            all.add(copy);
        }
        // Sort by arrival time so we can add them to the ready queue in the right order
        all.sort((a, b) -> Integer.compare(a.getArrivalTime(), b.getArrivalTime()));

        Queue<Process> readyQueue = new LinkedList<>();
        List<GanttEntry> ganttEntries = new ArrayList<>();

        // Track first-start time per process (needed for response time)
        Map<String, Integer> firstStartTime = new HashMap<>();
        // Track completion time per process
        Map<String, Integer> completionTimeMap = new HashMap<>();

        int currentTime = 0;
        int index = 0; // pointer into 'all', for adding newly-arrived processes to the queue
        int count = all.size();

        // Add any processes that arrive at time 0 first
        while (index < count && all.get(index).getArrivalTime() <= currentTime) {
            readyQueue.add(all.get(index));
            index++;
        }

        while (!readyQueue.isEmpty()) {

            Process current = readyQueue.poll();

            // Record first start time (for response time) only once per process
            firstStartTime.putIfAbsent(current.getProcessId(), currentTime);

            int runTime = Math.min(timeQuantum, current.getRemainingTime());
            int startTime = currentTime;
            int endTime = startTime + runTime;

            ganttEntries.add(new GanttEntry(current.getProcessId(), startTime, endTime));

            current.setRemainingTime(current.getRemainingTime() - runTime);
            currentTime = endTime;

            // After running, add any processes that have now arrived (before re-adding current if unfinished)
            while (index < count && all.get(index).getArrivalTime() <= currentTime) {
                readyQueue.add(all.get(index));
                index++;
            }

            if (current.getRemainingTime() > 0) {
                // Not finished yet — goes to the back of the queue
                readyQueue.add(current);
            } else {
                // Finished
                completionTimeMap.put(current.getProcessId(), currentTime);
            }
        }

        // Build ProcessResult list using original process data + computed times
        List<ProcessResult> processResults = new ArrayList<>();
        int totalWaiting = 0, totalTurnaround = 0, totalResponse = 0;

        for (Process p : processes) {
            int completion = completionTimeMap.get(p.getProcessId());
            int turnaround = completion - p.getArrivalTime();
            int waiting = turnaround - p.getBurstTime();
            int response = firstStartTime.get(p.getProcessId()) - p.getArrivalTime();

            processResults.add(new ProcessResult(
                    p.getProcessId(), p.getArrivalTime(), p.getBurstTime(),
                    completion, turnaround, waiting, response
            ));

            totalWaiting += waiting;
            totalTurnaround += turnaround;
            totalResponse += response;
        }

        double avgWaiting = (double) totalWaiting / count;
        double avgTurnaround = (double) totalTurnaround / count;
        double avgResponse = (double) totalResponse / count;

        return new CpuSchedulingResult(processResults, ganttEntries, avgWaiting, avgTurnaround, avgResponse);
    }

    @Override
    public String getName() {
        return "Round Robin";
    }
}