/**
 * TrackerUI.java: GUI for VintageVinylTracker
 * alec-shell
 * 11/25/2025
 */

package org.example;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
import com.github.scribejava.core.model.OAuth1RequestToken;
import org.example.gui.DBSearchUI;
import org.example.gui.DiscogsUI;
import org.example.gui.StatsUI;
import org.example.logic.DBAccess;
import org.example.logic.EventTriggers;
import org.example.logic.GenerateStats;
import org.example.temp.DiscogsAuthorization;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.ExecutionException;


public class Main extends JFrame {
    private final DBAccess dbAccess;
    private JTabbedPane tabsPane;
    private final DBSearchUI dbSearchUI;
    private final DiscogsUI discogsUI;
    private final StatsUI statsUI;
    private final DiscogsAuthorization discogsAuth;
    private final GenerateStats collectionStats;
    private final EventTriggers eventTriggers;
    private final HttpClient  httpClient;
    private final Keyring keyRing;

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
        this.discogsAuth = new DiscogsAuthorization();
        this.dbAccess = new DBAccess();
        this.collectionStats = new GenerateStats(discogsAuth, dbAccess);
        this.statsUI = new StatsUI(collectionStats);
        this.eventTriggers = new EventTriggers(statsUI);
        this.dbSearchUI = new DBSearchUI(discogsAuth, dbAccess, collectionStats,  eventTriggers);
        this.discogsUI = new DiscogsUI(discogsAuth, dbAccess, collectionStats, eventTriggers);
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
                if (!discogsAuth.hasToken()) {
                    spawnAuthorizationInput();
                } else if (!discogsAuth.hasAuthorization()) {
                    try {
                        discogsAuth.verifyAccess();
                    } catch (IOException | InterruptedException | ExecutionException ex) {
                        errorOptionPane(ex.getMessage());
                    }
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
            OAuth1RequestToken requestToken = discogsAuth.redirectAuthorization();
            String verifier = JOptionPane.showInputDialog("Enter verification code below:");
            if (verifier != null && !verifier.isEmpty()) {
                try {
                    discogsAuth.getAuthorization(verifier, requestToken);
                    discogsAuth.verifyAccess();
                } catch (IOException | ExecutionException | InterruptedException e) {
                    errorOptionPane(e.getMessage());
                }
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
