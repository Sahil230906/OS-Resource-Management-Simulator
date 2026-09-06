package com.ossim.models;

public class MemoryProcess {

    private String processId;
    private int size;

    public MemoryProcess(String processId, int size) {
        this.processId = processId;
        this.size = size;
    }

    // ===== Getters =====
    public String getProcessId() {
        return processId;
    }

    public int getSize() {
        return size;
    }

    // ===== Setters (needed for editable process lists in the UI) =====
    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "MemoryProcess{" +
                "id='" + processId + '\'' +
                ", size=" + size +
                '}';
    }
}