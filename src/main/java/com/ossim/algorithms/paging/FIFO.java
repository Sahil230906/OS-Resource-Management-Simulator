package com.ossim.algorithms.paging;

import com.ossim.models.PageReplacementResult;
import com.ossim.models.PageStepResult;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FIFO implements PageReplacementAlgorithm {

    @Override
    public PageReplacementResult run(List<Integer> referenceString, int frameCount) {

        // The frames themselves ARE the FIFO queue — oldest page is always
        // at the front, newest at the back. A hit never changes this order,
        // which is exactly what distinguishes FIFO from LRU.
        LinkedList<Integer> frames = new LinkedList<>();

        List<PageStepResult> steps = new ArrayList<>();
        int hitCount = 0;
        int faultCount = 0;

        for (int page : referenceString) {

            boolean hit = frames.contains(page);
            Integer evictedPage = null;

            if (hit) {
                hitCount++;
            } else {
                faultCount++;
                if (frames.size() >= frameCount) {
                    evictedPage = frames.removeFirst();
                }
                frames.addLast(page);
            }

            // Snapshot current frame contents, padded with nulls for
            // slots that haven't been filled yet
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
        return "FIFO";
    }
}