package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class ParseAPIResponse {

    public static ArrayList<Record> buildSearchQueryCollection(DiscogsAuthorization auth, String album, String artist, String year, String catNo) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Record> records = new ArrayList<>();
        int pageNo = 1;
        boolean hasNextPage = true;

        while (hasNextPage) {
            try {
                String body = DiscogsClient.searchQuery(auth, album, artist, year, catNo, pageNo++);
                JsonNode jsonNode = mapper.readTree(body);
                hasNextPage = jsonNode.get("pagination").get("urls").has("next");
                for (JsonNode node: jsonNode.path("results")) {
                    try {
                        records.add(convertNodeToRecord(node));
                    } catch (Exception e) {}
                }
            } catch (IOException | ExecutionException | InterruptedException e) {
                System.out.println("Could not complete search query: " + e.getMessage());
            }
        }
        return records;
    } // buildSearchQueryCollection()

    private static Record convertNodeToRecord(JsonNode node) {
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

    public static String buildPricingQueryCollection(DiscogsAuthorization auth, int id) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<String> pricingByCondition = new ArrayList<>();
        try {
            String json = DiscogsClient.getPriceSuggestions(auth, id);
            JsonNode jsonNode = mapper.readTree(json);
            addConditionalPrices(jsonNode, pricingByCondition);
        } catch (IOException | ExecutionException | InterruptedException e) {
            System.out.println("Could not complete pricing query: " + e.getMessage());
        }
        StringBuilder returnString = new StringBuilder();
        returnString.append("<html>");
        for (String price: pricingByCondition) {
            returnString.append(price);
            returnString.append("<br>");
        }
        returnString.append("</html>");
        return returnString.toString();
    } // buildPricingQueryCollection()

    private static void addConditionalPrices(JsonNode jsonNode, ArrayList<String> pricingByCondition) {
        String[] conditions = new String[]{
                "Mint (M)",
                "Near Mint (NM or M-)",
                "Very Good Plus (VG+)",
                "Very Good (VG)",
                "Good Plus (G+)",
                "Good (G)",
                "Fair (F)",
                "Poor (P)"
        };
        for (String condition: conditions) {
            String price = jsonNode.path(condition).path("value").asText();
            pricingByCondition.add(condition + ": $" + String.format("%.2f", Double.parseDouble(price)));
        }
    } // addConditionalPrices()

} // ParseAPIResponse class
