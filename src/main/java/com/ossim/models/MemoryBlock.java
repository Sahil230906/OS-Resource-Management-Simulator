package com.ossim.models;

public class MemoryBlock {

    private String blockId;
    private int size;

    // Used internally by allocation algorithms
    private boolean allocated;
    private String allocatedProcessId;
    private int allocatedProcessSize;

    public MemoryBlock(String blockId, int size) {
        this.blockId = blockId;
        this.size = size;
        this.allocated = false;
        this.allocatedProcessId = null;
        this.allocatedProcessSize = 0;
    }

    // ===== Getters =====
    public String getBlockId() {
        return blockId;
    }

    public int getSize() {
        return size;
    }

    public boolean isAllocated() {
        return allocated;
    }

    public String getAllocatedProcessId() {
        return allocatedProcessId;
    }

    public int getAllocatedProcessSize() {
        return allocatedProcessSize;
    }

    public int getInternalFragmentation() {
        return allocated ? size - allocatedProcessSize : 0;
    }

    // ===== Setters (editable partition table + used internally by algorithms) =====
    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setAllocated(boolean allocated) {
        this.allocated = allocated;
    }

    public void setAllocatedProcessId(String allocatedProcessId) {
        this.allocatedProcessId = allocatedProcessId;
    }

    public void setAllocatedProcessSize(int allocatedProcessSize) {
        this.allocatedProcessSize = allocatedProcessSize;
    }

    @Override
    public String toString() {
        return "MemoryBlock{" +
                "id='" + blockId + '\'' +
                ", size=" + size +
                ", allocated=" + allocated +
                ", process='" + allocatedProcessId + '\'' +
                '}';
    }
}