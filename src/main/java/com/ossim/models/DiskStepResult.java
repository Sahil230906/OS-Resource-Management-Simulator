package com.ossim.models;

public class DiskStepResult {

    private int fromTrack;
    private int toTrack;
    private int movement;
    private boolean boundaryMove;

    // Original constructor — unchanged, still used by FCFS and SSTF,
    // which have no boundary moves. Defaults boundaryMove to false.
    public DiskStepResult(int fromTrack, int toTrack, int movement) {
        this(fromTrack, toTrack, movement, false);
    }

    // Used by SCAN/C-SCAN for the boundary-touching moves
    public DiskStepResult(int fromTrack, int toTrack, int movement, boolean boundaryMove) {
        this.fromTrack = fromTrack;
        this.toTrack = toTrack;
        this.movement = movement;
        this.boundaryMove = boundaryMove;
    }

    public int getFromTrack() {
        return fromTrack;
    }

    public int getToTrack() {
        return toTrack;
    }

    public int getMovement() {
        return movement;
    }

    public boolean isBoundaryMove() {
        return boundaryMove;
    }

    @Override
    public String toString() {
        return "DiskStepResult{" +
                "from=" + fromTrack +
                ", to=" + toTrack +
                ", movement=" + movement +
                ", boundary=" + boundaryMove +
                '}';
    }
}