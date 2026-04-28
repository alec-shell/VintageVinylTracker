package org.example.Controller;

import org.example.Controller.Client.ProxyClient;
import org.example.Config.Constants;
import org.example.DTO.Record;
import org.example.DTO.SearchResult;
import org.example.Service.ParseService;

import java.util.ArrayList;
import java.util.HashMap;

public class APIController {

    public static HashMap<String, Double> getPricingHashMap(ProxyClient proxyClient, int id) {
        String json = proxyClient.getPriceSuggestions(id);
        return ParseService.parseJson(json);
    } // getPricingHashMap()

    public static ArrayList<Record> getDiscogsSearchResults(ProxyClient proxyClient, String album,
                                                            String artist, String year, String catNo) {
        ArrayList<Record> records = new ArrayList<>();
        boolean hasNextPage = true;
        int page = 1;

        while (hasNextPage && records.size() < Constants.RESULTS_CAP) {
            String json = proxyClient.getSearchQuery(album, artist, year, catNo, page);
            SearchResult result = ParseService.parseSearchResults(json);
            hasNextPage = result.hasNextPage();
            if (hasNextPage) {page++;}
            records.addAll(result.getRecords());
        }
        return records;
    } // getDiscogsSearchResults()

} // APIController
