package org.example.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Client.ProxyClient;
import org.example.Config.Constants;
import org.example.DTO.Record;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class ParseAPIResponse {

    public static ArrayList<org.example.DTO.Record> buildSearchQueryCollection(ProxyClient proxyClient, String album, String artist, String year, String catNo) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<org.example.DTO.Record> records = new ArrayList<>();
        int pageNo = 1;
        boolean hasNextPage = true;

        while (hasNextPage && records.size() < Constants.RESULTS_CAP) {
            try {
                String body = proxyClient.getSearchQuery(album, artist, year, catNo, pageNo++);
                JsonNode jsonNode = mapper.readTree(body);
                hasNextPage = jsonNode.get("pagination").get("urls").has("next");
                for (JsonNode node: jsonNode.path("results")) {
                    try {
                        records.add(convertNodeToRecord(node));
                    } catch (Exception e) {
                        System.out.println("Skipping invalid record: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.out.println("Could not complete search query: " + e.getMessage());
            }
        }
        return records;
    } // buildSearchQueryCollection()

    private static org.example.DTO.Record convertNodeToRecord(JsonNode node) {
        int id = Integer.parseInt(node.path("id").asText());
        String[] title = node.path("title").asText().split(" - ");
        String artist = title[0];
        String album = title[1];
        String year = node.path("year").asText();
        String country = node.path("country").asText();
        String catNo = node.path("catno").asText();
        String thumbUrl = node.path("cover_image").asText();

        return new Record(id,
                artist,
                album,
                year,
                country,
                catNo,
                thumbUrl,
                false,
                0.0,
                0.0,
                "NONE");
    } // convertNodeToRecord()

    public static HashMap<String, Double> buildPricingQueryCollection(ProxyClient proxyClient, int id) {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Double> priceMap = new HashMap<>();
        try {
            String json = proxyClient.getPriceSuggestions(id);
            JsonNode jsonNode = mapper.readTree(json);
            addConditionalPrices(jsonNode, priceMap);
        } catch (IOException e) {
            System.out.println("Could not complete pricing query: " + e.getMessage());
        }
        return priceMap;
    } // buildPricingQueryCollection()

    private static void addConditionalPrices(JsonNode jsonNode, HashMap<String, Double> priceMap) {
        for (int i = 0; i < Constants.pricingConditions.length; i++) {
            Double price = Double.parseDouble(jsonNode.path(Constants.pricingConditions[i]).path("value").asText());
            priceMap.put(Constants.pricingConditions[i], price);
        }
    } // addConditionalPrices()

} // ParseAPIResponse class
