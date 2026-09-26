package com.ossim.algorithms.cpu;

import com.ossim.models.CpuSchedulingResult;
import com.ossim.models.Process;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CpuSchedulingTest {

    private ObservableList<Process> processes(Process... procs) {
        return FXCollections.observableArrayList(procs);
    }

    // ===== FCFS =====

    @Test
    void fcfs_threeProcesses_basicCase() {
        ObservableList<Process> input = processes(
                new Process("P1", 0, 5, 2),
                new Process("P2", 1, 3, 1),
                new Process("P3", 2, 7, 3)
        );

        CpuSchedulingResult result = new FCFS().run(input);

        // P1: 0-5, P2: 5-8, P3: 8-15
        assertEquals(3.33, result.getAverageWaitingTime(), 0.01);
        assertEquals(8.33, result.getAverageTurnaroundTime(), 0.01);
        assertEquals(3.33, result.getAverageResponseTime(), 0.01);
    }

    @Test
    void fcfs_singleProcess_completesImmediately() {
        ObservableList<Process> input = processes(
                new Process("P1", 0, 5, 1)
        );

        CpuSchedulingResult result = new FCFS().run(input);

        assertEquals(0.0, result.getAverageWaitingTime(), 0.01);
        assertEquals(5.0, result.getAverageTurnaroundTime(), 0.01);
        assertEquals(0.0, result.getAverageResponseTime(), 0.01);
    }

    @Test
    void fcfs_allArriveAtTimeZero_runsInGivenOrder() {
        ObservableList<Process> input = processes(
                new Process("P1", 0, 4, 1),
                new Process("P2", 0, 2, 1),
                new Process("P3", 0, 6, 1)
        );

        CpuSchedulingResult result = new FCFS().run(input);

        // P1: 0-4, P2: 4-6, P3: 6-12
        assertEquals(3.33, result.getAverageWaitingTime(), 0.01);
        assertEquals(7.33, result.getAverageTurnaroundTime(), 0.01);
        assertEquals(3.33, result.getAverageResponseTime(), 0.01);
    }

    // ===== SJF =====

    @Test
    void sjf_picksShortestJobAmongArrived_reordersVsArrival() {
        ObservableList<Process> input = processes(
                new Process("P1", 0, 7, 1),
                new Process("P2", 2, 4, 1),
                new Process("P3", 4, 1, 1)
        );

        CpuSchedulingResult result = new SJF().run(input);

        // P1: 0-7 (only one arrived). At t=7, P2(4) and P3(1) arrived — P3 runs first.
        // P3: 7-8, P2: 8-12
        assertEquals(3.0, result.getAverageWaitingTime(), 0.01);
        assertEquals(7.0, result.getAverageTurnaroundTime(), 0.01);
        assertEquals(3.0, result.getAverageResponseTime(), 0.01);
    }

    // ===== Priority Scheduling =====

    @Test
    void priorityScheduling_lowerNumberRunsFirst() {
        ObservableList<Process> input = processes(
                new Process("P1", 0, 4, 3),
                new Process("P2", 1, 3, 1),
                new Process("P3", 2, 2, 2)
        );

        CpuSchedulingResult result = new PriorityScheduling().run(input);

        // P1: 0-4 (only one arrived). At t=4, P2(prio1) and P3(prio2) arrived — P2 runs first.
        // P2: 4-7, P3: 7-9
        assertEquals(2.67, result.getAverageWaitingTime(), 0.01);
        assertEquals(5.67, result.getAverageTurnaroundTime(), 0.01);
        assertEquals(2.67, result.getAverageResponseTime(), 0.01);
    }

    // ===== Round Robin =====

    @Test
    void roundRobin_alternatesBetweenTwoProcesses() {
        ObservableList<Process> input = processes(
                new Process("P1", 0, 4, 1),
                new Process("P2", 0, 4, 1)
        );

        CpuSchedulingResult result = new RoundRobin(2).run(input);

        // P1: 0-2, P2: 2-4, P1: 4-6 (done), P2: 6-8 (done)
        assertEquals(3.0, result.getAverageWaitingTime(), 0.01);
        assertEquals(7.0, result.getAverageTurnaroundTime(), 0.01);
        assertEquals(1.0, result.getAverageResponseTime(), 0.01);
    }
}