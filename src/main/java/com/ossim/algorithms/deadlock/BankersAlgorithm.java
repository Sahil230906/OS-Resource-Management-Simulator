package com.ossim.algorithms.deadlock;

import com.ossim.models.BankersResult;
import com.ossim.models.BankersStepResult;

import java.util.ArrayList;
import java.util.List;

public class BankersAlgorithm {

    public BankersResult run(int numProcesses, int numResources,
                              List<List<Integer>> allocation,
                              List<List<Integer>> max,
                              List<Integer> available) {

        // Need[i][j] = Max[i][j] - Allocation[i][j]
        List<List<Integer>> need = new ArrayList<>();
        for (int i = 0; i < numProcesses; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < numResources; j++) {
                row.add(max.get(i).get(j) - allocation.get(i).get(j));
            }
            need.add(row);
        }

        List<Integer> work = new ArrayList<>(available);
        boolean[] finished = new boolean[numProcesses];
        List<Integer> safeSequence = new ArrayList<>();
        List<BankersStepResult> steps = new ArrayList<>();

        // Repeatedly scan for any unfinished process whose Need fits within
        // Work. Keep looping as long as at least one process finishes each
        // pass — stop when a full pass finds nothing left to finish.
        boolean progress = true;
        while (progress) {
            progress = false;

            for (int i = 0; i < numProcesses; i++) {
                if (finished[i] && canProceed(need.get(i), work)) {
                    // unreachable guard removed below; kept simple
                }
                if (!finished[i] && canProceed(need.get(i), work)) {
                    List<Integer> workBefore = new ArrayList<>(work);

                    for (int j = 0; j < numResources; j++) {
                        work.set(j, work.get(j) + allocation.get(i).get(j));
                    }

                    List<Integer> workAfter = new ArrayList<>(work);

                    finished[i] = true;
                    safeSequence.add(i);
                    steps.add(new BankersStepResult(i, workBefore, workAfter));
                    progress = true;
                }
            }
        }

        boolean safe = true;
        for (boolean f : finished) {
            if (!f) {
                safe = false;
                break;
            }
        }

        return new BankersResult(safe, safeSequence, steps, need);
    }

    private boolean canProceed(List<Integer> need, List<Integer> work) {
        for (int j = 0; j < need.size(); j++) {
            if (need.get(j) > work.get(j)) {
                return false;
            }
        }
        return true;
    }
}