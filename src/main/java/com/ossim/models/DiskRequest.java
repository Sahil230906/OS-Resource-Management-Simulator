package com.ossim.models;

public class DiskRequest {

    private int trackNumber;

    public DiskRequest(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    @Override
    public String toString() {
        return "DiskRequest{" +
                "trackNumber=" + trackNumber +
                '}';
    }
}