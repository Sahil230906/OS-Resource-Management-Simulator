package com.ossim.algorithms.cpu;

import com.ossim.models.CpuSchedulingResult;
import com.ossim.models.Process;

import java.util.List;

public interface CpuSchedulingAlgorithm {

    /**
     * Runs the scheduling algorithm on the given list of processes
     * and returns the full result (per-process stats, Gantt entries, averages).
     *
     * @param processes the list of processes to schedule (input is never modified)
     * @return a CpuSchedulingResult containing everything the UI needs
     */
    CpuSchedulingResult run(List<Process> processes);

    /**
     * Human-readable name of the algorithm, used in the UI (e.g. algorithm selector, comparison table).
     */
    String getName();
}