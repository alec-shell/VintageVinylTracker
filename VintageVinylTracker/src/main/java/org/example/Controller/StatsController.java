package org.example.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.example.Client.ProxyClient;
import org.example.DTO.CollectionStats;
import org.example.DTO.Record;
import org.example.Service.StatsService;

import java.util.ArrayList;

public class StatsController {
    private static final ObjectMapper mapper = new JsonMapper();

    public static CollectionStats getStats(DatabaseController databaseController) {
        ArrayList<Record> ownedRecords = databaseController.searchRecordEntries(null,
                null, null, null, "true");
        CollectionStats initStats = StatsService.parseOwnedAlbums(ownedRecords);
        initStats.setIsUpdating(databaseController.checkForOwnedPricingUpdate());
        return initStats;
    } // initStats()

    public static void updateOwnedValues(ArrayList<Record> ownedRecords, ProxyClient proxyClient,
                                                      DatabaseController databaseController) {
        for (Record record: ownedRecords) {
            updateRecordValue(record, proxyClient, databaseController);
        }
        StatsService.parseOwnedAlbums(ownedRecords);
        databaseController.updateMetaDate();
    } // updateOwnedValues()

    private static void updateRecordValue(Record record, ProxyClient proxyClient, DatabaseController databaseController) {
        try {
            String priceResponse = proxyClient.getPriceSuggestions(record.getID());
            JsonNode nodes = mapper.readTree(priceResponse);
            double newPrice = Double.parseDouble(nodes.get(record.getCondition()).get("value").asText());
            record.setValue(newPrice);
            databaseController.updateRecordPrice(record.getID(), newPrice);
        } catch (JsonProcessingException e) {
            System.out.println("Error updating record price... " + e.getMessage());
        }
    } // updateRecordValue()

} // StatsController
