package org.example.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.example.client.ProxyClient;

import java.util.ArrayList;
import java.util.List;

public class GenerateStats {
    private int albumCount = 0;
    private double totalInvested = 0;
    private double totalValue = 0;
    private Record mostValuableRecord = null;
    private Record leastValuableRecord = null;
    private final ProxyClient proxyClient;
    private final DBAccess dbAccess;
    private final JsonMapper mapper;
    private ArrayList<Record> ownedRecords;
    private boolean isUpdating = false;

    public GenerateStats(ProxyClient proxyClient, DBAccess dbAccess, JsonMapper mapper) {
        this.proxyClient = proxyClient;
        this.dbAccess = dbAccess;
        this.mapper = mapper;
        parseOwnedAlbums();
        isUpdating = dbAccess.checkForUpdate();
    } // constructor

    public void parseOwnedAlbums() {
        resetValues();
        ownedRecords = dbAccess.searchRecordEntries(null,
                null, null, null, "true");
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

    private void updateRecordValue(Record record) {
        try {
            String priceResponse = proxyClient.getPriceSuggestions(record.getID());
            JsonNode nodes = mapper.readTree(priceResponse);
            double newPrice = Double.parseDouble(nodes.get(record.getCondition()).get("value").asText());
            dbAccess.updateRecordPrice(record.getID(), newPrice);
        } catch (JsonProcessingException e) {
            System.out.println("Error updating record price... " + e.getMessage());
        }
    } // updateRecordValue()

    public void updateOwnedValues() {
        List<Record> ownedCopies =  new ArrayList<>(ownedRecords);
        for (Record copy: ownedCopies) {
            updateRecordValue(copy);
        }
        parseOwnedAlbums();
        dbAccess.updateMetaDate();
    } // updateOwnedValues()

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

    public boolean isUpdating() {
        return isUpdating;
    } // isUpdating()

} // GenerateStats class
