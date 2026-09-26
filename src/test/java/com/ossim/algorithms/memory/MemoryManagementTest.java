package com.ossim.algorithms.memory;

import com.ossim.models.MemoryAllocationResult;
import com.ossim.models.MemoryBlock;
import com.ossim.models.MemoryProcess;
import com.ossim.models.MemoryProcessResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MemoryManagementTest {

    private ObservableList<MemoryBlock> classicPartitions() {
        return FXCollections.observableArrayList(
                new MemoryBlock("B1", 100),
                new MemoryBlock("B2", 500),
                new MemoryBlock("B3", 200),
                new MemoryBlock("B4", 300),
                new MemoryBlock("B5", 600)
        );
    }

    private ObservableList<MemoryProcess> classicProcesses() {
        return FXCollections.observableArrayList(
                new MemoryProcess("P1", 212),
                new MemoryProcess("P2", 417),
                new MemoryProcess("P3", 112),
                new MemoryProcess("P4", 426)
        );
    }

    private MemoryProcessResult findResult(List<MemoryProcessResult> results, String processId) {
        return results.stream()
                .filter(r -> r.getProcessId().equals(processId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No result found for " + processId));
    }

    // ===== First Fit =====

    @Test
    void firstFit_classicExample() {
        MemoryAllocationResult result = new FirstFit().run(classicPartitions(), classicProcesses());

        // P1(212) -> B2(500), P2(417) -> B5(600), P3(112) -> B3(200), P4(426) -> fails (no block left)
        assertEquals(3, result.getAllocatedCount());
        assertEquals(1, result.getFailedCount());
        assertEquals(559, result.getTotalInternalFragmentation()); // 288 + 183 + 88

        List<MemoryProcessResult> results = result.getProcessResults();

        assertEquals("B2", findResult(results, "P1").getBlockId());
        assertEquals("B5", findResult(results, "P2").getBlockId());
        assertEquals("B3", findResult(results, "P3").getBlockId());
        assertFalse(findResult(results, "P4").isAllocated());
    }

    // ===== Best Fit =====

    @Test
    void bestFit_classicExample() {
        MemoryAllocationResult result = new BestFit().run(classicPartitions(), classicProcesses());

        // P1(212) -> B4(300), P2(417) -> B2(500), P3(112) -> B3(200), P4(426) -> B5(600)
        assertEquals(4, result.getAllocatedCount());
        assertEquals(0, result.getFailedCount());
        assertEquals(433, result.getTotalInternalFragmentation()); // 88 + 83 + 88 + 174

        List<MemoryProcessResult> results = result.getProcessResults();

        assertEquals("B4", findResult(results, "P1").getBlockId());
        assertEquals("B2", findResult(results, "P2").getBlockId());
        assertEquals("B3", findResult(results, "P3").getBlockId());
        assertEquals("B5", findResult(results, "P4").getBlockId());
    }

    // ===== Worst Fit =====

    @Test
    void worstFit_classicExample() {
        MemoryAllocationResult result = new WorstFit().run(classicPartitions(), classicProcesses());

        // P1(212) -> B5(600), P2(417) -> B2(500), P3(112) -> B4(300), P4(426) -> fails (only B1=100, B3=200 left)
        assertEquals(3, result.getAllocatedCount());
        assertEquals(1, result.getFailedCount());
        assertEquals(659, result.getTotalInternalFragmentation()); // 388 + 83 + 188

        List<MemoryProcessResult> results = result.getProcessResults();

        assertEquals("B5", findResult(results, "P1").getBlockId());
        assertEquals("B2", findResult(results, "P2").getBlockId());
        assertEquals("B4", findResult(results, "P3").getBlockId());
        assertFalse(findResult(results, "P4").isAllocated());
    }

    // ===== Edge cases =====

    @Test
    void exactSizeMatch_producesZeroFragmentation() {
        ObservableList<MemoryBlock> partitions = FXCollections.observableArrayList(new MemoryBlock("B1", 100));
        ObservableList<MemoryProcess> processes = FXCollections.observableArrayList(new MemoryProcess("P1", 100));

        MemoryAllocationResult result = new FirstFit().run(partitions, processes);

        assertEquals(1, result.getAllocatedCount());
        assertEquals(0, result.getTotalInternalFragmentation());
        assertEquals(0, findResult(result.getProcessResults(), "P1").getInternalFragmentation());
    }

    @Test
    void processLargerThanAllBlocks_fails() {
        ObservableList<MemoryBlock> partitions = FXCollections.observableArrayList(new MemoryBlock("B1", 50));
        ObservableList<MemoryProcess> processes = FXCollections.observableArrayList(new MemoryProcess("P1", 100));

        MemoryAllocationResult result = new FirstFit().run(partitions, processes);

        assertEquals(0, result.getAllocatedCount());
        assertEquals(1, result.getFailedCount());
        assertFalse(findResult(result.getProcessResults(), "P1").isAllocated());
        assertNull(findResult(result.getProcessResults(), "P1").getBlockId());
    }
}