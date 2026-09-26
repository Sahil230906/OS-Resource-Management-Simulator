package com.ossim.algorithms.disk;

import com.ossim.models.DiskSchedulingResult;
import com.ossim.models.DiskStepResult;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class DiskSchedulingTest {

    private List<Integer> classicRequests() {
        return Arrays.asList(98, 183, 37, 122, 14, 124, 65, 67);
    }

    private List<Integer> serviceOrder(DiskSchedulingResult result) {
        return result.getSteps().stream()
                .filter(step -> !step.isBoundaryMove())
                .map(DiskStepResult::getToTrack)
                .collect(Collectors.toList());
    }

    // ===== FCFS =====

    @Test
    void fcfs_classicExample_servicesInGivenOrder() {
        DiskSchedulingResult result = new FCFS().run(50, classicRequests(), 199, true);

        assertEquals(643, result.getTotalHeadMovement());
        assertEquals(80.375, result.getAverageHeadMovement(), 0.01);
        assertEquals(Arrays.asList(98, 183, 37, 122, 14, 124, 65, 67), serviceOrder(result));
    }

    // ===== SSTF =====

    @Test
    void sstf_classicExample_alwaysPicksClosestRemaining() {
        DiskSchedulingResult result = new SSTF().run(50, classicRequests(), 199, true);

        assertEquals(205, result.getTotalHeadMovement());
        assertEquals(25.625, result.getAverageHeadMovement(), 0.01);
        assertEquals(Arrays.asList(37, 14, 65, 67, 98, 122, 124, 183), serviceOrder(result));
    }

    // ===== SCAN =====

    @Test
    void scan_towardZero_servicesInElevatorOrder() {
        DiskSchedulingResult result = new SCAN().run(50, classicRequests(), 199, true);

        // Sweeps down to 0 (servicing 37, 14 on the way), reverses, sweeps up (servicing the rest)
        assertEquals(Arrays.asList(37, 14, 65, 67, 98, 122, 124, 183), serviceOrder(result));

        // Assumes SCAN touches both disk boundaries (0 and 199): 50->0 (50) + 0->199 (199) = 249
        assertEquals(249, result.getTotalHeadMovement());
    }

    // ===== C-SCAN =====

    @Test
    void cscan_towardZero_servicesInCircularOrder() {
        DiskSchedulingResult result = new CSCAN().run(50, classicRequests(), 199, true);

        // Sweeps down to 0 (servicing 37, 14), jumps to 199, continues down (servicing the rest)
        assertEquals(Arrays.asList(37, 14, 183, 124, 122, 98, 67, 65), serviceOrder(result));

        // Assumes the jump 0->199 counts toward total movement: 50 + 199 + (199-65) = 383
        assertEquals(383, result.getTotalHeadMovement());
    }

    // ===== Edge cases =====

    @Test
    void singleRequest_fcfs_movesDirectlyToIt() {
        DiskSchedulingResult result = new FCFS().run(50, Arrays.asList(120), 199, true);

        assertEquals(70, result.getTotalHeadMovement());
        assertEquals(70.0, result.getAverageHeadMovement(), 0.01);
    }

    @Test
    void duplicateTrackRequests_sstf_handlesWithoutError() {
        DiskSchedulingResult result = new SSTF().run(50, Arrays.asList(60, 60, 40), 199, true);

        assertEquals(3, result.getSteps().stream().filter(s -> !s.isBoundaryMove()).count());
        assertTrue(result.getTotalHeadMovement() >= 0);
    }
}