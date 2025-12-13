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
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class DiscogsAuthorization {
    private OAuth1AccessToken accessToken;
    private final Path configPath = Path.of("config.txt");
    private final Path secretsPath = Path.of(".secrets.txt");
    private String username = null;

    protected DiscogsAuthorization() {
        OAuth10aService service = null;
        try {
            String[] config = parseConfig();
            service = new ServiceBuilder(config[0])
                    .apiSecret(config[1])
                    .build(DiscogsAPI.getInstance());
            if (!checkForToken()) {
                try {
                    getAuthorization(service);
                } catch (IOException | ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not build service: " + e.getMessage());
        }
        try {
            verifyAccess(service);
            if (username != null) System.out.println("Welcome " +  username);
            else System.out.println("Failed to verify...");
        }catch (IOException | ExecutionException | InterruptedException e) {
            System.out.println("Could not verify access: " + e.getMessage());
        }
    } // constructor

    public void getAuthorization(OAuth10aService service) throws IOException, ExecutionException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        final OAuth1RequestToken requestToken = service.getRequestToken();
        // redirect user to Discogs authorization url
        try {
            Desktop.getDesktop().browse(new URI(service.getAuthorizationUrl(requestToken)));
        } catch (IOException | URISyntaxException e) {
            System.out.println("Please visit: " + service.getAuthorizationUrl(requestToken));
        }
        // get user's verifier and request access token from Discogs
        System.out.println("Enter verifier: ");
        String verifier = scanner.nextLine();
        accessToken = service.getAccessToken(requestToken, verifier);
        saveToken(accessToken.getToken(), accessToken.getTokenSecret());
        scanner.close();
    } // getAuthorization()

    private void verifyAccess(OAuth10aService service) throws IOException, ExecutionException, InterruptedException {
        final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.discogs.com/oauth/identity");
        service.signRequest(accessToken, request);
        final Response response = service.execute(request);
        String[] body = response.getBody().split(",");
        for (String line : body) {
            if (line.contains("username")) {
                username = line.split(":")[1];
            }
        }
    } // verifyAccess

    private boolean checkForToken() {
        if (!Files.exists(configPath)) return false;
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
    } // saveToken()

    private void buildToken() throws IOException {
        String[] secrets = Files.readAllLines(secretsPath).toArray(new String[2]);
        accessToken = new OAuth1AccessToken(secrets[0], secrets[1]);
    } // buildToken()

    private String[] parseConfig() throws IOException {
        return Files.readAllLines(configPath).toArray(new String[2]);
    } // parseConfig()

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        DiscogsAuthorization auth = new DiscogsAuthorization();
    } // main()

} // DiscogsAuthorization class
