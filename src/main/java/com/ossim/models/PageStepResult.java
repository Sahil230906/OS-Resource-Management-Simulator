package com.ossim.models;

import java.util.List;

public class PageStepResult {

    private int pageRequested;
    private boolean hit;
    private List<Integer> frameState;
    private Integer evictedPage;

    public PageStepResult(int pageRequested, boolean hit, List<Integer> frameState, Integer evictedPage) {
        this.pageRequested = pageRequested;
        this.hit = hit;
        this.frameState = frameState;
        this.evictedPage = evictedPage;
    }

    public int getPageRequested() {
        return pageRequested;
    }

    public boolean isHit() {
        return hit;
    }

    public List<Integer> getFrameState() {
        return frameState;
    }

    public Integer getEvictedPage() {
        return evictedPage;
    }

    @Override
    public String toString() {
        return "PageStepResult{" +
                "page=" + pageRequested +
                ", hit=" + hit +
                ", frames=" + frameState +
                ", evicted=" + evictedPage +
                '}';
    }
}