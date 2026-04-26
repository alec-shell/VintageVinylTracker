package org.example.DTO;

import java.util.ArrayList;

public class SearchResult {
    private boolean hasNextPage;
    private ArrayList<Record> records;

    public SearchResult(boolean hasNextPage, ArrayList<Record> records) {
        this.hasNextPage = hasNextPage;
        this.records = records;
    } // constructor

    // getters
    public boolean hasNextPage() {
        return hasNextPage;
    } // hasNextPage()

    public ArrayList<Record> getRecords() {
        return records;
    } // getRecords()

} // SearchResult
