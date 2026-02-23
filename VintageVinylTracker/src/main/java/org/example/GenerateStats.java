package org.example;

import javax.swing.*;
import java.util.ArrayList;

public class GenerateStats {
    private int albumCount = 0;
    private double totalInvested = 0;
    private double totalValue = 0;
    private Record mostValuableRecord = null;
    private Record leastValuableRecord = null;
    private final DiscogsAuthorization discogsAuth;
    private final DBAccess dbAccess;
    private ArrayList<Record> ownedRecords;

    public GenerateStats(DiscogsAuthorization discogsAuth,  DBAccess dbAccess) {
        this.discogsAuth = discogsAuth;
        this.dbAccess = dbAccess;
        parseOwnedAlbums();

    } // constructor

    public void parseOwnedAlbums() {
        resetValues();
        ownedRecords = dbAccess.searchRecordEntries(null,
                null, null, null, true);
        albumCount = ownedRecords.size();

        if  (albumCount > 0) {
            mostValuableRecord = ownedRecords.getFirst();
            leastValuableRecord = ownedRecords.getFirst();
        }
        for (Record record : ownedRecords) {
            totalValue += record.getValue();
            totalInvested += record.getPurchasePrice();
            if (mostValuableRecord.getValue() < record.getValue()) mostValuableRecord = record;
            else if (leastValuableRecord.getValue() > record.getValue()) leastValuableRecord = record;
        }
    } // retrieveOwnedAlbums()

    private void resetValues() {
        albumCount = 0;
        totalInvested = 0;
        totalValue = 0;
        mostValuableRecord = null;
        leastValuableRecord = null;
    } // resetValues()

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

} // GenerateStats class
