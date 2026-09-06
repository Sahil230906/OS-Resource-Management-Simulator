package com.ossim.models;

public class MemoryProcessResult {

    private String processId;
    private int processSize;
    private boolean allocated;
    private String blockId;
    private int blockSize;
    private int internalFragmentation;

    public MemoryProcessResult(String processId, int processSize, boolean allocated,
                                String blockId, int blockSize, int internalFragmentation) {
        this.processId = processId;
        this.processSize = processSize;
        this.allocated = allocated;
        this.blockId = blockId;
        this.blockSize = blockSize;
        this.internalFragmentation = internalFragmentation;
    }

    // ===== Getters =====
    public String getProcessId() {
        return processId;
    }

    public int getProcessSize() {
        return processSize;
    }

    public boolean isAllocated() {
        return allocated;
    }

    public String getBlockId() {
        return blockId;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public int getInternalFragmentation() {
        return internalFragmentation;
    }

    @Override
    public String toString() {
        return "MemoryProcessResult{" +
                "id='" + processId + '\'' +
                ", size=" + processSize +
                ", allocated=" + allocated +
                ", block='" + blockId + '\'' +
                '}';
    }
}