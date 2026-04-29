package org.example.DTO;


import java.util.ArrayList;

public class CollectionStats {
    private int albumCount = 0;
    private double totalInvested = 0;
    private double totalValue = 0;
    private Record mostValuableRecord;
    private Record leastValuableRecord;
    private ArrayList<Record> ownedRecords;
    boolean isUpdating = false;

    public CollectionStats(int albumCount, double totalInvested, double totalValue,
                           Record mostValuableRecord, Record leastValuableRecord,
                           ArrayList<Record> ownedRecords) {
        this.albumCount = albumCount;
        this.totalInvested = totalInvested;
        this.totalValue = totalValue;
        this.mostValuableRecord = mostValuableRecord;
        this.leastValuableRecord = leastValuableRecord;
        this.ownedRecords = ownedRecords;
    } // constructor

    // getters

    public int getOwnedCount() {
        return albumCount;
    } // getOwnedCount()

    public double getTotalInvested() {
        return totalInvested;
    } // getTotalInvested()

    public double getTotalValue() {
        return totalValue;
    } // getTotalValue()

    public Record getMostValuableRecord() {
        return mostValuableRecord;
    } // getMostValuableRecord()

    public Record getLeastValuableRecord() {
        return leastValuableRecord;
    } // getLeastValuableRecord()

    public ArrayList<Record> getOwnedRecords() {return ownedRecords;} // getOwnedRecords()

    public boolean getIsUpdating() {return isUpdating;}

    // setters

    public void setOwnedCount(int albumCount) {this.albumCount = albumCount;}

    public void setTotalInvested(double totalInvested) {this.totalInvested = totalInvested;}

    public void setTotalValue(double totalValue) {this.totalValue = totalValue;}

    public void setMostValuableRecord(Record mostValuableRecord) {this.mostValuableRecord = mostValuableRecord;}

    public void setLeastValuableRecord(Record leastValuableRecord) {this.leastValuableRecord = leastValuableRecord;}

    public void setOwnedRecords(ArrayList<Record> ownedRecords) {this.ownedRecords = ownedRecords;}

    public void setIsUpdating(boolean isUpdating) {this.isUpdating = isUpdating;}

} // CollectionStats
