package com.ossim.algorithms.disk;

import com.ossim.models.DiskSchedulingResult;

import java.util.List;

public interface DiskSchedulingAlgorithm {

    /**
     * @param initialHead   starting head position
     * @param requests      requested tracks, in the order given
     * @param diskSize      highest valid track number (e.g. 199 for a 0–199 disk)
     * @param towardZero    for SCAN/C-SCAN: true if the head moves toward track 0
     *                      first, false if it moves toward diskSize first.
     *                      Ignored by FCFS and SSTF.
     */
    DiskSchedulingResult run(int initialHead, List<Integer> requests, int diskSize, boolean towardZero);

    String getName();
}