package org.example.Logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Client.ProxyClient;
import org.example.Config.Constants;

import java.io.IOException;
import java.util.ArrayList;


public class ParseAPIResponse {
    public static double[] selectionPrices = new double[8];

    public static ArrayList<Record> buildSearchQueryCollection(ProxyClient proxyClient, String album, String artist, String year, String catNo) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Record> records = new ArrayList<>();
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

    public static String buildPricingQueryCollection(ProxyClient proxyClient, int id) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<String> pricingByCondition = new ArrayList<>();
        try {
            String json = proxyClient.getPriceSuggestions(id);
            JsonNode jsonNode = mapper.readTree(json);
            addConditionalPrices(jsonNode, pricingByCondition);
        } catch (IOException e) {
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
        for (int i = 0; i < Constants.pricingConditions.length; i++) {
            Double price = Double.parseDouble(jsonNode.path(Constants.pricingConditions[i]).path("value").asText());
            selectionPrices[i] = price;
            pricingByCondition.add(Constants.pricingConditions[i] + ": $" + String.format("%.2f", price));
        }
    } // addConditionalPrices()

} // ParseAPIResponse class
