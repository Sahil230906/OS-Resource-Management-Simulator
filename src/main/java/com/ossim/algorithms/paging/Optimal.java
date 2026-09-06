package com.ossim.algorithms.paging;

import com.ossim.models.PageReplacementResult;
import com.ossim.models.PageStepResult;

import java.util.ArrayList;
import java.util.List;

public class Optimal implements PageReplacementAlgorithm {

    @Override
    public PageReplacementResult run(List<Integer> referenceString, int frameCount) {

        List<Integer> frames = new ArrayList<>();
        List<PageStepResult> steps = new ArrayList<>();

        int hitCount = 0;
        int faultCount = 0;

        for (int i = 0; i < referenceString.size(); i++) {
            int page = referenceString.get(i);

            boolean hit = frames.contains(page);
            Integer evictedPage = null;

            if (hit) {
                hitCount++;
            } else {
                faultCount++;
                if (frames.size() >= frameCount) {
                    int victimIndex = findVictimIndex(frames, referenceString, i);
                    evictedPage = frames.remove(victimIndex);
                }
                frames.add(page);
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

    /**
     * Finds the frame whose page is used farthest in the future
     * (or never used again, treated as infinitely far). Ties go to
     * whichever candidate appears first in the frame list.
     */
    private int findVictimIndex(List<Integer> frames, List<Integer> referenceString, int currentIndex) {
        int victimIndex = -1;
        int farthestUse = -1;

        for (int f = 0; f < frames.size(); f++) {
            int candidatePage = frames.get(f);
            int nextUse = findNextUse(referenceString, candidatePage, currentIndex);

            if (nextUse > farthestUse) {
                farthestUse = nextUse;
                victimIndex = f;
            }
        }

        return victimIndex;
    }

    private int findNextUse(List<Integer> referenceString, int page, int currentIndex) {
        for (int j = currentIndex + 1; j < referenceString.size(); j++) {
            if (referenceString.get(j) == page) {
                return j;
            }
        }
        return Integer.MAX_VALUE; // never used again
    }

    @Override
    public String getName() {
        return "Optimal";
    }
}