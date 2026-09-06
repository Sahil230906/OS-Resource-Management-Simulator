package com.ossim.algorithms.memory;

import com.ossim.models.MemoryAllocationResult;
import com.ossim.models.MemoryBlock;
import com.ossim.models.MemoryProcess;
import com.ossim.models.MemoryProcessResult;

import java.util.ArrayList;
import java.util.List;

public class FirstFit implements MemoryAllocationAlgorithm {

    @Override
    public MemoryAllocationResult run(List<MemoryBlock> blocks, List<MemoryProcess> processes) {

        // Step 1: Work on fresh copies of the blocks so the original partition
        // list stays untouched (needed so Comparison can run multiple algorithms
        // on the same starting partitions without state bleeding between them)
        List<MemoryBlock> workingBlocks = new ArrayList<>();
        for (MemoryBlock b : blocks) {
            workingBlocks.add(new MemoryBlock(b.getBlockId(), b.getSize()));
        }

        List<MemoryProcessResult> processResults = new ArrayList<>();

        int allocatedCount = 0;
        int failedCount = 0;

        // Step 2: For each process, in the given order, scan blocks in the
        // given order and allocate to the FIRST block that fits
        for (MemoryProcess process : processes) {

            MemoryBlock chosenBlock = null;

            for (MemoryBlock block : workingBlocks) {
                if (!block.isAllocated() && block.getSize() >= process.getSize()) {
                    chosenBlock = block;
                    break;
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
                // No block fit this process — valid outcome, not an error
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

        // Step 3: Compute summary stats from the final block state
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
        return "First Fit";
    }
}