package com.ossim.algorithms.paging;

import com.ossim.models.PageReplacementResult;

import java.util.List;

public interface PageReplacementAlgorithm {

    PageReplacementResult run(List<Integer> referenceString, int frameCount);

    String getName();
}