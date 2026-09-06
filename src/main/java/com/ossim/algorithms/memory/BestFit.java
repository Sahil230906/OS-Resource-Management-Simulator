package com.ossim.algorithms.memory;

import com.ossim.models.MemoryAllocationResult;
import com.ossim.models.MemoryBlock;
import com.ossim.models.MemoryProcess;
import com.ossim.models.MemoryProcessResult;

import java.util.ArrayList;
import java.util.List;

public class BestFit implements MemoryAllocationAlgorithm {

    @Override
    public MemoryAllocationResult run(List<MemoryBlock> blocks, List<MemoryProcess> processes) {

        List<MemoryBlock> workingBlocks = new ArrayList<>();
        for (MemoryBlock b : blocks) {
            workingBlocks.add(new MemoryBlock(b.getBlockId(), b.getSize()));
        }

        List<MemoryProcessResult> processResults = new ArrayList<>();

        int allocatedCount = 0;
        int failedCount = 0;

        for (MemoryProcess process : processes) {

            // Step: among all unallocated blocks that fit, find the SMALLEST one
            MemoryBlock chosenBlock = null;

            for (MemoryBlock block : workingBlocks) {
                if (!block.isAllocated() && block.getSize() >= process.getSize()) {
                    if (chosenBlock == null || block.getSize() < chosenBlock.getSize()) {
                        chosenBlock = block;
                    }
                }
            }

            if (chosenBlock != null) {
                chosenBlock.setAllocated(true);
                chosenBlock.setAllocatedProcessId(process.getProcessId());
                chosenBlock.setAllocatedProcessSize(process.getSize());

                processResults.add(new MemoryProcessResult(
                        process.getProcessId(),
                        process.getSize(),
                        true,
                        chosenBlock.getBlockId(),
                        chosenBlock.getSize(),
                        chosenBlock.getInternalFragmentation()
                ));

                allocatedCount++;
            } else {
                processResults.add(new MemoryProcessResult(
                        process.getProcessId(),
                        process.getSize(),
                        false,
                        null,
                        0,
                        0
                ));

                failedCount++;
            }
        }

        int totalInternalFragmentation = 0;
        int totalFreeMemory = 0;

        for (MemoryBlock block : workingBlocks) {
            if (block.isAllocated()) {
                totalInternalFragmentation += block.getInternalFragmentation();
            } else {
                totalFreeMemory += block.getSize();
            }
        }

        return new MemoryAllocationResult(
                processResults,
                workingBlocks,
                allocatedCount,
                failedCount,
                totalInternalFragmentation,
                totalFreeMemory
        );
    }

    @Override
    public String getName() {
        return "Best Fit";
    }
}