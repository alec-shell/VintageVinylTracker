package org.example.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.example.Client.ProxyClient;
import org.example.DTO.CollectionStats;
import org.example.DTO.Record;
import org.example.Service.DBAccessService;

import java.util.ArrayList;

public class StatsController {
    private static final ObjectMapper mapper = new JsonMapper();

    public static CollectionStats getInitialStats(DBAccessService dbAccessService) {
        CollectionStats initStats = parseOwnedAlbums(dbAccessService);
        initStats.setIsUpdating(dbAccessService.checkForOwnedPricingUpdate());
        return initStats;
    } // initStats()

    public static void updateOwnedValues(ArrayList<Record> ownedRecords, ProxyClient proxyClient,
                                                      DBAccessService dbAccessService) {
        for (Record record: ownedRecords) {
            updateRecordValue(record, proxyClient, dbAccessService);
        }
        parseOwnedAlbums(dbAccessService);
        dbAccessService.updateMetaDate();
    } // updateOwnedValues()

    private static void updateRecordValue(Record record, ProxyClient proxyClient, DBAccessService dbAccess) {
        try {
            String priceResponse = proxyClient.getPriceSuggestions(record.getID());
            JsonNode nodes = mapper.readTree(priceResponse);
            double newPrice = Double.parseDouble(nodes.get(record.getCondition()).get("value").asText());
            record.setValue(newPrice);
            dbAccess.updateRecordPrice(record.getID(), newPrice);
        } catch (JsonProcessingException e) {
            System.out.println("Error updating record price... " + e.getMessage());
        }
    } // updateRecordValue()

    private static CollectionStats parseOwnedAlbums(DBAccessService dbAccess) {
        ArrayList<Record> ownedRecords = dbAccess.searchRecordEntries(null,
                null, null, null, "true");
        int albumCount = ownedRecords.size();
        Record mostValuableRecord = null;
        Record leastValuableRecord = null;
        double totalValue = 0;
        double totalInvested = 0;

        if  (albumCount > 0) {
            mostValuableRecord = ownedRecords.getFirst();
            leastValuableRecord = ownedRecords.getFirst();
        }
        for (Record record : ownedRecords) {
            totalValue += record.getValue();
            totalInvested += record.getPurchasePrice();
            if (mostValuableRecord.getValue() < record.getValue()) mostValuableRecord = record;
            if (leastValuableRecord.getValue() > record.getValue()) leastValuableRecord = record;
        }
        return new CollectionStats(albumCount, totalInvested, totalValue,
                mostValuableRecord, leastValuableRecord, ownedRecords);
    } // retrieveOwnedAlbums()

}
