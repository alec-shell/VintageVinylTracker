/**
 * TrackerUI.java: GUI for VintageVinylTracker
 * alec-shell
 * 11/25/2025
 */

package org.example;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
import org.example.client.AuthorizationClient;
import org.example.client.ProxyClient;
import org.example.gui.DBSearchUI;
import org.example.gui.DiscogsUI;
import org.example.gui.StatsUI;
import org.example.logic.DBAccess;
import org.example.logic.EventTriggers;
import org.example.logic.GenerateStats;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.time.Duration;


public class Main extends JFrame {
    private final DBAccess dbAccess;
    private JTabbedPane tabsPane;
    private final DBSearchUI dbSearchUI;
    private final DiscogsUI discogsUI;
    private final StatsUI statsUI;
    AuthorizationClient authorizationClient;
    private final GenerateStats collectionStats;
    private final EventTriggers eventTriggers;
    private final HttpClient  httpClient;
    private final Keyring keyRing;
    private final ProxyClient proxyClient;
    private final JsonMapper mapper =  new JsonMapper();

    public Main() {
        this.setTitle("Vintage Vinyl");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 600);
        this.setLayout(new BorderLayout());
        this.httpClient = HttpClient
                .newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
        try {
            this.keyRing = Keyring.create();
        } catch (BackendNotSupportedException e) {
            throw new RuntimeException(e);
        }
        this.authorizationClient = new AuthorizationClient(httpClient, keyRing, mapper);
        this.proxyClient = new ProxyClient(httpClient, keyRing, mapper);
        this.dbAccess = new DBAccess();
        this.collectionStats = new GenerateStats(proxyClient, dbAccess, mapper);
        this.statsUI = new StatsUI(collectionStats);
        this.eventTriggers = new EventTriggers(statsUI);
        this.dbSearchUI = new DBSearchUI(proxyClient, dbAccess, collectionStats,  eventTriggers);
        this.discogsUI = new DiscogsUI(proxyClient, dbAccess, collectionStats, eventTriggers);
        buildTabbedPane();
        this.add(tabsPane, BorderLayout.CENTER);
    } // constructor

    private void buildTabbedPane() {
        tabsPane = new JTabbedPane(JTabbedPane.LEFT);
        tabsPane.add("Stats", statsUI);
        tabsPane.add("Discogs", discogsUI);
        tabsPane.add("Search Database",  dbSearchUI);
        addTabListener();
    } // buildTabbedPane()

    private void addTabListener() {
        tabsPane.addChangeListener(_ -> {
            String tabTitle = tabsPane.getTitleAt(tabsPane.getSelectedIndex());
            if (tabTitle.equals("Discogs")) {
                if (!authorizationClient.hasAuthorization()) {
                    spawnAuthorizationInput();
                }
            }
        });
    } // addTabListener()

    private void spawnAuthorizationInput() {
        int authBoxChoice = JOptionPane.showConfirmDialog(this,
                "Sync your discogs account?",
                "Discogs Account Sync",
                JOptionPane.YES_NO_OPTION);
        if (authBoxChoice == JOptionPane.YES_OPTION) {
            try {
                authorizationClient.getRequestToken();
                authorizationClient.getAuthorizationURL();
                Desktop.getDesktop().browse(new URI(authorizationClient.getURL()));
                String verifier = JOptionPane.showInputDialog("Enter verification code below:");
                if (verifier != null && !verifier.isEmpty()) {
                    try {
                        authorizationClient.getUserToken(verifier);
                        if (authorizationClient.hasAuthorization()) {
                            JOptionPane.showMessageDialog(this, "Discogs Account Sync Successful!");
                        }
                    } catch (IOException | InterruptedException e) {
                        errorOptionPane(e.getMessage());
                    }
                }
            } catch (IOException | InterruptedException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    } // spawnAuthorizationInput()

    private void errorOptionPane(String message) {
        JOptionPane.showMessageDialog(this, "Could not connect to Discogs: " + message,
                "Authorization Error", JOptionPane.ERROR_MESSAGE);
    } // errorOptionPane()

    public static void main(String[] args) {
        JFrame frame = new Main();
        frame.setVisible(true);
    } // main()

} // TrackerUI class
