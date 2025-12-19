package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ParseAPIResponse {

    protected static ArrayList<Record> buildSearchQueryCollection(DiscogsAuthorization auth, String album, String artist, String year, String catNo) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Record> records = new ArrayList<>();
        int pageNo = 1;
        boolean hasNextPage = true;
        while (hasNextPage) {
            try {
                String body = DiscogsClient.searchQuery(auth, album, artist, year, catNo, pageNo++);
                JsonNode jsonNode = mapper.readTree(body);
                hasNextPage = jsonNode.get("pagination").has("next");
                for (JsonNode node: jsonNode.path("results")) {
                    records.add(convertNodeToRecord(node));
                }
                System.out.println(jsonNode.toPrettyString());
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
        String thumbUrl = node.path("thumb").asText();

        return new Record(id,
                artist,
                album,
                year,
                country,
                null,
                catNo,
                thumbUrl,
                false);
    } // convertNodeToRecord()

} // ParseAPIResponse class
