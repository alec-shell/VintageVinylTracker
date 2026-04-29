package org.example.Service;

import org.example.DTO.CollectionStats;
import org.example.DTO.Record;

import java.util.ArrayList;

public class StatsService {
    public static CollectionStats parseOwnedAlbums(ArrayList<Record> ownedRecords) {
        int albumCount = ownedRecords.size();
        Record mostValuableRecord = null;
        Record leastValuableRecord = null;
        double totalValue = 0;
        double totalInvested = 0;

        for (Record record : ownedRecords) {
            totalValue += record.getValue();
            totalInvested += record.getPurchasePrice();
            if (mostValuableRecord == null || mostValuableRecord.getValue() < record.getValue()) {
                mostValuableRecord = record;
            }
            if (leastValuableRecord == null || leastValuableRecord.getValue() > record.getValue()) {
                leastValuableRecord = record;
            }
        }
        return new CollectionStats(albumCount, totalInvested, totalValue,
                mostValuableRecord, leastValuableRecord, ownedRecords);
    } // parseOwnedAlbums()

} // StatsService
