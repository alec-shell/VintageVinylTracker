package org.example.Client;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.javakeyring.Keyring;
import com.github.javakeyring.PasswordAccessException;
import org.example.DTO.PricingRequest;
import org.example.DTO.SearchRequest;
import org.example.Config.URIConfig;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ProxyClient {
    private final HttpClient httpClient;
    private final Keyring keyRing;
    private final JsonMapper mapper;


    public ProxyClient(HttpClient httpClient, Keyring keyring, JsonMapper mapper) {
        this.httpClient = httpClient;
        this.keyRing = keyring;
        this.mapper = mapper;
    } // constructor

    public String getSearchQuery(String album, String artist, String year, String catNo, int pageNo) {
        try {
            String json = mapper
                    .writeValueAsString(new SearchRequest(keyRing.getPassword("VintageVinyl", "userToken"),
                    keyRing.getPassword("VintageVinyl", "userSecret"),
                    artist, album, year, catNo, pageNo));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URIConfig.SEARCH_URI)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (PasswordAccessException | IOException | InterruptedException e) {
            throw  new RuntimeException(e);
        }
    } // getSearchQuery()

    public String getPriceSuggestions(int id) {
        try {
            String json = mapper
                    .writeValueAsString(new PricingRequest(id,
                            keyRing.getPassword("VintageVinyl", "userToken"),
                            keyRing.getPassword("VintageVinyl", "userSecret")));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URIConfig.PRICING_URI)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (PasswordAccessException | IOException | InterruptedException e) {
            throw  new RuntimeException(e);
        }
    } // getPricingSuggestions()



} // DiscogsClient class
