package com.ossim.algorithms.paging;

import com.ossim.models.PageReplacementResult;
import com.ossim.models.PageStepResult;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LRU implements PageReplacementAlgorithm {

    @Override
    public PageReplacementResult run(List<Integer> referenceString, int frameCount) {

        // Front = least recently used, back = most recently used.
        // Unlike FIFO, a HIT also reorders this list — that's the whole
        // difference between the two algorithms.
        LinkedList<Integer> frames = new LinkedList<>();

        List<PageStepResult> steps = new ArrayList<>();
        int hitCount = 0;
        int faultCount = 0;

        for (Integer page : referenceString) {

            boolean hit = frames.contains(page);
            Integer evictedPage = null;

            if (hit) {
                hitCount++;
                frames.remove(page);
                frames.addLast(page);
            } else {
                faultCount++;
                if (frames.size() >= frameCount) {
                    evictedPage = frames.removeFirst();
                }
                frames.addLast(page);
            }

            List<Integer> frameState = new ArrayList<>(frames);
            while (frameState.size() < frameCount) {
                frameState.add(null);
            }

            steps.add(new PageStepResult(page, hit, frameState, evictedPage));
        }

        int total = referenceString.size();
        double hitRatio = total == 0 ? 0.0 : (double) hitCount / total;
        double faultRatio = total == 0 ? 0.0 : (double) faultCount / total;

        return new PageReplacementResult(steps, hitCount, faultCount, hitRatio, faultRatio);
    }

    @Override
    public String getName() {
        return "LRU";
    }
}