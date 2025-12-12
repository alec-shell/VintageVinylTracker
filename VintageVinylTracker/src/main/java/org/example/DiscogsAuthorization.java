package org.example;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth10aService;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class DiscogsAuthorization {
    private static boolean browserOpened = true;
    private static String verifier = null;

    public static void getAuthorization() throws IOException, ExecutionException, InterruptedException {
            String[] config = parseConfig();
            final OAuth10aService service = new ServiceBuilder(config[0])
                    .apiSecret(config[1])
                    .build(DiscogsAPI.getInstance());
            final OAuth1RequestToken requestToken = service.getRequestToken();

            try {
                Desktop.getDesktop().browse(new URI(service.getAuthorizationUrl(requestToken)));
            } catch (IOException | URISyntaxException e) {
                browserOpened = false;
                System.out.println("Please visit: " + service.getAuthorizationUrl(requestToken));
            }
            System.out.println("Enter verifier: " + browserOpened);
            Scanner scanner = new Scanner(System.in);
            verifier = scanner.nextLine();
            final OAuth1AccessToken accessToken = service.getAccessToken(requestToken, verifier);

            final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.discogs.com/oauth/identity");
            service.signRequest(accessToken, request);
            final Response response = service.execute(request);
            System.out.println(response.getBody());
    } // getAuthorization()

    private static String[] parseConfig() throws IOException {
        Path path = Path.of("config.txt");
        return Files.readAllLines(path).toArray(new String[2]);
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        getAuthorization();
    }


} // org.example.DiscogsAuthorization class
