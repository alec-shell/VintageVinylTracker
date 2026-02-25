/**
 * TrackerUI.java: GUI for VintageVinylTracker
 * alec-shell
 * 11/25/2025
 */

package org.example;

import com.github.scribejava.core.model.OAuth1RequestToken;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class MainUI extends JFrame {
    private final DBAccess dbAccess;
    private JTabbedPane tabsPane;
    private final DBSearchUI dbSearchUI;
    private final DiscogsUI discogsUI;
    private final StatsUI statsUI;
    private final DiscogsAuthorization discogsAuth;
    private final GenerateStats collectionStats;
    private final EventTriggers eventTriggers;

    public MainUI() {
        this.setTitle("Vintage Vinyl");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 600);
        this.setLayout(new BorderLayout());
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
        JFrame frame = new MainUI();
        frame.setVisible(true);
    } // main()

} // TrackerUI class
