package com.ossim.models;

import java.util.List;

public class MemoryAllocationResult {

    private List<MemoryProcessResult> processResults;
    private List<MemoryBlock> finalBlockState;
    private int allocatedCount;
    private int failedCount;
    private int totalInternalFragmentation;
    private int totalFreeMemory;

    public MemoryAllocationResult(List<MemoryProcessResult> processResults,
                                   List<MemoryBlock> finalBlockState,
                                   int allocatedCount,
                                   int failedCount,
                                   int totalInternalFragmentation,
                                   int totalFreeMemory) {
        this.processResults = processResults;
        this.finalBlockState = finalBlockState;
        this.allocatedCount = allocatedCount;
        this.failedCount = failedCount;
        this.totalInternalFragmentation = totalInternalFragmentation;
        this.totalFreeMemory = totalFreeMemory;
    }

    public List<MemoryProcessResult> getProcessResults() {
        return processResults;
    }

    public List<MemoryBlock> getFinalBlockState() {
        return finalBlockState;
    }

    public int getAllocatedCount() {
        return allocatedCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public int getTotalInternalFragmentation() {
        return totalInternalFragmentation;
    }

    public int getTotalFreeMemory() {
        return totalFreeMemory;
    }
}