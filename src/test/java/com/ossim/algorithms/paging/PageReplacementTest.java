package com.ossim.algorithms.paging;

import com.ossim.models.PageReplacementResult;
import com.ossim.models.PageStepResult;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PageReplacementTest {

    private List<Integer> classicReferenceString() {
        return Arrays.asList(7, 0, 1, 2, 0, 3, 0, 4);
    }

    // ===== FIFO =====

    @Test
    void fifo_classicPrefix_sevenFaults() {
        PageReplacementResult result = new FIFO().run(classicReferenceString(), 3);

        assertEquals(1, result.getHitCount());
        assertEquals(7, result.getFaultCount());
        assertEquals(0.125, result.getHitRatio(), 0.001);
        assertEquals(0.875, result.getFaultRatio(), 0.001);

        List<PageStepResult> steps = result.getSteps();
        assertEquals(8, steps.size());

        // Only step 5 (page 0, index 4) is a hit — 0 is still in frames from step 2
        boolean[] expectedHits = {false, false, false, false, true, false, false, false};
        for (int i = 0; i < 8; i++) {
            assertEquals(expectedHits[i], steps.get(i).isHit(), "Mismatch at step " + (i + 1));
        }

        // Evictions are fully deterministic for FIFO (oldest-inserted goes first)
        assertNull(steps.get(0).getEvictedPage());
        assertNull(steps.get(1).getEvictedPage());
        assertNull(steps.get(2).getEvictedPage());
        assertEquals(7, steps.get(3).getEvictedPage()); // step 4: page 2 evicts 7
        assertEquals(0, steps.get(5).getEvictedPage()); // step 6: page 3 evicts 0
        assertEquals(1, steps.get(6).getEvictedPage()); // step 7: page 0 evicts 1
        assertEquals(2, steps.get(7).getEvictedPage()); // step 8: page 4 evicts 2
    }

    // ===== LRU =====

    @Test
    void lru_classicPrefix_sixFaults() {
        PageReplacementResult result = new LRU().run(classicReferenceString(), 3);

        assertEquals(2, result.getHitCount());
        assertEquals(6, result.getFaultCount());
        assertEquals(0.25, result.getHitRatio(), 0.001);
        assertEquals(0.75, result.getFaultRatio(), 0.001);

        List<PageStepResult> steps = result.getSteps();

        // Hits at step 5 (page 0) and step 7 (page 0 again)
        boolean[] expectedHits = {false, false, false, false, true, false, true, false};
        for (int i = 0; i < 8; i++) {
            assertEquals(expectedHits[i], steps.get(i).isHit(), "Mismatch at step " + (i + 1));
        }

        // LRU has no ties here — fully deterministic
        assertEquals(7, steps.get(3).getEvictedPage()); // step 4: evicts 7 (least recently used)
        assertEquals(1, steps.get(5).getEvictedPage()); // step 6: evicts 1
        assertEquals(2, steps.get(7).getEvictedPage()); // step 8: evicts 2
    }

    // ===== Optimal =====

    @Test
    void optimal_classicPrefix_sixFaults_matchesOrBeatsLru() {
        PageReplacementResult result = new Optimal().run(classicReferenceString(), 3);

        assertEquals(2, result.getHitCount());
        assertEquals(6, result.getFaultCount());
        assertEquals(0.25, result.getHitRatio(), 0.001);
        assertEquals(0.75, result.getFaultRatio(), 0.001);

        List<PageStepResult> steps = result.getSteps();

        // Hit/fault pattern is the same regardless of how Optimal breaks ties
        boolean[] expectedHits = {false, false, false, false, true, false, true, false};
        for (int i = 0; i < 8; i++) {
            assertEquals(expectedHits[i], steps.get(i).isHit(), "Mismatch at step " + (i + 1));
        }

        // No eviction happens while frames are still filling up (steps 1-3)
        assertNull(steps.get(0).getEvictedPage());
        assertNull(steps.get(1).getEvictedPage());
        assertNull(steps.get(2).getEvictedPage());
    }

    // ===== Edge cases =====

    @Test
    void singleUniquePage_repeatedReference_allHitsAfterFirst() {
        List<Integer> refs = Arrays.asList(5, 5, 5, 5);

        PageReplacementResult result = new FIFO().run(refs, 2);

        assertEquals(3, result.getHitCount());
        assertEquals(1, result.getFaultCount());
    }

    @Test
    void framesNeverFill_everyDistinctPageIsAFault_noEvictions() {
        List<Integer> refs = Arrays.asList(1, 2, 3);

        PageReplacementResult result = new FIFO().run(refs, 5); // 5 frames, only 3 distinct pages

        assertEquals(0, result.getHitCount());
        assertEquals(3, result.getFaultCount());
        for (PageStepResult step : result.getSteps()) {
            assertNull(step.getEvictedPage());
        }
    }
}