package org.example;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth10aService;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

public class DiscogsAuthorization {
    private OAuth1AccessToken accessToken;
    private final Path configPath = Path.of("config.txt");
    private final Path secretsPath = Path.of(".secrets.txt");
    private OAuth10aService service = null;
    private String username = null;
    private boolean hasToken = checkForToken();
    private boolean hasAuthorization = false;

    protected DiscogsAuthorization() {
        try {
            String[] config = parseConfig();
            service = new ServiceBuilder(config[0])
                    .apiSecret(config[1])
                    .build(ScribeDiscogsAPI.getInstance());
            if (hasToken) {
                try {
                    hasAuthorization = verifyAccess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Could not build service: " + e.getMessage());
        }
    } // constructor

    protected OAuth1RequestToken redirectAuthorization() {
        try {
            final OAuth1RequestToken requestToken = service.getRequestToken();
            try {
                Desktop.getDesktop().browse(new URI(service.getAuthorizationUrl(requestToken)));
            } catch (IOException | URISyntaxException e) {
                System.out.println("Please visit: " + service.getAuthorizationUrl(requestToken));
            }
            return requestToken;
        } catch (IOException | InterruptedException | ExecutionException e) {
            System.out.println("Could not get request token: " + e.getMessage());
        }
        return null;
    } // redirectAuthorization

    protected void getAuthorization(String verifier, OAuth1RequestToken requestToken) throws IOException, ExecutionException, InterruptedException {
        // get user's verifier and request access token from Discogs
        accessToken = service.getAccessToken(requestToken, verifier);
        if (accessToken.getClass() == OAuth1AccessToken.class) {
            saveToken(accessToken.getToken(), accessToken.getTokenSecret());
        }
    } // getAuthorization()

    protected boolean verifyAccess() throws IOException, ExecutionException, InterruptedException {
        final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.discogs.com/oauth/identity");
        service.signRequest(accessToken, request);
        final Response response = service.execute(request);
        String[] body = response.getBody().split(",");
        for (String line : body) {
            if (line.contains("username")) {
                hasAuthorization = true;
                username = line.split(":")[1].strip();
                username = username.substring(1, username.length() - 2);
            }
        }
        return hasAuthorization;
    } // verifyAccess

    protected boolean checkForToken() {
        if (!Files.exists(secretsPath)) return false;
        else {
            try {
                buildToken();
                return accessToken != null;
            } catch (IOException e) {
                System.out.println("Could not build token: " + e.getMessage());
                return false;
            }
        }
    } // checkForToken()

    private void saveToken(String token, String secret) throws IOException {
        Files.createFile(secretsPath);
        Files.writeString(secretsPath,token + "\n" + secret);
        hasToken = true;
    } // saveToken()

    private void buildToken() throws IOException {
        String[] secrets = Files.readAllLines(secretsPath).toArray(new String[2]);
        accessToken = new OAuth1AccessToken(secrets[0], secrets[1]);
    } // buildToken()

    private String[] parseConfig() throws IOException {
        return Files.readAllLines(configPath).toArray(new String[2]);
    } // parseConfig()

    protected boolean hasAuthorization() {
        return hasAuthorization;
    } // isAuthorized()

    protected boolean hasToken() {
        return hasToken;
    }

    protected String getUsername() {
        return username;
    } // getUsername()

    protected String getAuthUrl(OAuth1RequestToken requestToken) {
        return service.getAuthorizationUrl(requestToken);
    } // getAuthUrl()

    protected OAuth10aService getService() {
        return service;
    } // getService()

    protected OAuth1AccessToken getAccessToken() {
        return accessToken;
    }
} // DiscogsAuthorization class
