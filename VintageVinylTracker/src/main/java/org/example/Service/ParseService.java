package org.example.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Config.Constants;
import org.example.DTO.Record;
import org.example.DTO.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ParseService {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static HashMap<String, Double> parseJson(String json) {
        HashMap<String, Double> priceMap = new HashMap<>();
        try {
            JsonNode jsonNode = mapper.readTree(json);
            parseConditionalPrices(jsonNode, priceMap);
        } catch (IOException e) {
            System.out.println("Could not complete pricing query: " + e.getMessage());
        }
        return priceMap;
    } // parseJson()

    private static void parseConditionalPrices(JsonNode jsonNode, HashMap<String, Double> priceMap) {
        for (int i = 0; i < Constants.pricingConditions.length; i++) {
            Double price = Double.parseDouble(jsonNode.path(Constants.pricingConditions[i]).path("value").asText());
            priceMap.put(Constants.pricingConditions[i], price);
        }
    } // parseConditionalPrices()

    public static org.example.DTO.Record parseNodeToRecord(JsonNode node) {
        int id = Integer.parseInt(node.path("id").asText());
        String[] title = node.path("title").asText().split(" - ");
        String artist = title[0];
        String album = title[1];
        String year = node.path("year").asText();
        String country = node.path("country").asText();
        String catNo = node.path("catno").asText();
        String thumbUrl = node.path("cover_image").asText();

        return new Record(id, artist, album, year, country, catNo,
                thumbUrl, false, 0.0, 0.0, "NONE");
    } // convertNodeToRecord()

    public static SearchResult parseSearchResults(String json) {
        ArrayList<Record> records = new ArrayList<>();
        boolean hasNextPage = false;
        try {
            JsonNode jsonNode = mapper.readTree(json);
            hasNextPage = jsonNode.get("pagination").get("urls").has("next");
            for (JsonNode node : jsonNode.path("results")) {
                try {
                    records.add(ParseService.parseNodeToRecord(node));
                } catch (Exception e) {
                    System.out.println("Skipping invalid record: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Could not complete search query: " + e.getMessage());
        }
        return new SearchResult(hasNextPage, records);
    } // parseSearchResults()

} // ParseService
