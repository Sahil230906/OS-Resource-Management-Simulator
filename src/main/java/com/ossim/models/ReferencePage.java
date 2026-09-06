package com.ossim.models;

public class ReferencePage {

    private int pageNumber;

    public ReferencePage(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public String toString() {
        return "ReferencePage{" +
                "pageNumber=" + pageNumber +
                '}';
    }
}