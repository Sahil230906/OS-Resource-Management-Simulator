package com.ossim.algorithms.deadlock;

import com.ossim.models.BankersResult;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BankersAlgorithmTest {

    @Test
    void classicExample_isSafe_withExpectedSequence() {
        List<List<Integer>> allocation = Arrays.asList(
                Arrays.asList(0, 1, 0),
                Arrays.asList(2, 0, 0),
                Arrays.asList(3, 0, 2),
                Arrays.asList(2, 1, 1),
                Arrays.asList(0, 0, 2)
        );
        List<List<Integer>> max = Arrays.asList(
                Arrays.asList(7, 5, 3),
                Arrays.asList(3, 2, 2),
                Arrays.asList(9, 0, 2),
                Arrays.asList(2, 2, 2),
                Arrays.asList(4, 3, 3)
        );
        List<Integer> available = Arrays.asList(3, 3, 2);

        BankersResult result = new BankersAlgorithm().run(5, 3, allocation, max, available);

        assertTrue(result.isSafe());
        assertEquals(Arrays.asList(1, 3, 4, 0, 2), result.getSafeSequence());
    }

    @Test
    void noAvailableResources_withOutstandingNeed_isUnsafe() {
        // Both processes still need resources, but none are available and none are allocated
        // enough to finish on their own — no process can ever proceed.
        List<List<Integer>> allocation = Arrays.asList(
                Arrays.asList(0),
                Arrays.asList(0)
        );
        List<List<Integer>> max = Arrays.asList(
                Arrays.asList(1),
                Arrays.asList(1)
        );
        List<Integer> available = Arrays.asList(0);

        BankersResult result = new BankersAlgorithm().run(2, 1, allocation, max, available);

        assertFalse(result.isSafe());
    }

    @Test
    void allProcessesAlreadyWithinAvailable_singleProcess_isTriviallySafe() {
        List<List<Integer>> allocation = Arrays.asList(
                Arrays.asList(0)
        );
        List<List<Integer>> max = Arrays.asList(
                Arrays.asList(5)
        );
        List<Integer> available = Arrays.asList(5);

        BankersResult result = new BankersAlgorithm().run(1, 1, allocation, max, available);

        assertTrue(result.isSafe());
        assertEquals(Arrays.asList(0), result.getSafeSequence());
    }
}