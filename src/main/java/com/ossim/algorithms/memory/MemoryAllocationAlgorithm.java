package com.ossim.algorithms.memory;

import com.ossim.models.MemoryAllocationResult;
import com.ossim.models.MemoryBlock;
import com.ossim.models.MemoryProcess;

import java.util.List;

public interface MemoryAllocationAlgorithm {

    MemoryAllocationResult run(List<MemoryBlock> blocks, List<MemoryProcess> processes);

    String getName();
}