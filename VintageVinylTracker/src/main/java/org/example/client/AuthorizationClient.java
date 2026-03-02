package org.example.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.javakeyring.Keyring;
import com.github.javakeyring.PasswordAccessException;
import org.example.DTO.AuthTokenRequest;
import org.example.DTO.VerifierURLRequest;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class AuthorizationClient {
    private final HttpClient httpClient;
    private final AuthTokenRequest requestTknDTO =  new AuthTokenRequest();
    private final JsonMapper mapper = new JsonMapper();
    private String url = null;
    private Keyring keyRing;

    public AuthorizationClient(HttpClient httpClient, Keyring keyRing) {
        this.httpClient = httpClient;
        this.keyRing = keyRing;
    } // constructor

    public void getRequestToken() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URIConfig.REQUEST_TOKEN_URI)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode nodes = mapper.readTree(response.body());
        requestTknDTO.token = nodes.get("token").asText();
        requestTknDTO.secret = nodes.get("secret").asText();
    } // getRequestToken()

    public void getAuthorizationURL() throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(new VerifierURLRequest(requestTknDTO.token, requestTknDTO.secret));
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URIConfig.REQUEST_VERIFIER_URI)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode nodes = mapper.readTree(response.body());
        url =  nodes.get("url").asText();
    } // getAuthorizationURL()

    public void getUserToken(String verifier) throws IOException, InterruptedException {
        requestTknDTO.verifier = verifier;
        String json = mapper.writeValueAsString(requestTknDTO);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URIConfig.AUTH_TOKEN_URI)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode nodes = mapper.readTree(response.body());
        try {
            storeUserSecret(nodes);
        } catch (PasswordAccessException e) {
            e.printStackTrace();
        }
    } // getUserToken()

    private void storeUserSecret(JsonNode nodes) throws PasswordAccessException {
        keyRing.setPassword("VintageVinyl", "userToken", nodes.get("token").asText());
        keyRing.setPassword("VintageVinyl", "userSecret", nodes.get("secret").asText());
    } // storeUserSecret()

    public String getURL() {
        return url;
    } // getURL()

} // AuthorizationClient class
