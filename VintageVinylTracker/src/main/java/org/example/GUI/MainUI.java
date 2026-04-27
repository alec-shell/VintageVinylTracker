package org.example.GUI;

import org.example.Client.AuthorizationClient;
import org.example.Client.ProxyClient;
import org.example.Config.Constants;
import org.example.Config.URIConfig;
import org.example.GUI.async.AsyncCalls;
import org.example.Service.DBAccessService;
import org.example.GUI.statsUpdate.EventTriggers;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainUI extends JFrame {
    private JTabbedPane tabsPane;
    private final DBSearchUI dbSearchUI;
    private final DiscogsUI discogsUI;
    private final StatsUI statsUI;
    private boolean sessionAuth = false;
    private final AuthorizationClient authorizationClient;

    public MainUI(ProxyClient proxyClient,
                  DBAccessService dbAccess, AuthorizationClient authorizationClient,
                  AsyncCalls asyncCalls, EventTriggers eventTriggers) {
        this.setTitle("Vintage Vinyl");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 600);
        this.setLayout(new BorderLayout());
        this.authorizationClient = authorizationClient;
        final URL defaultURL = getClass().getResource(URIConfig.defaultIconImgPath);
        setDefaultThumbNail(defaultURL);
        this.statsUI = new StatsUI(asyncCalls, dbAccess, proxyClient);
        eventTriggers.setStatsUI(statsUI);
        this.dbSearchUI = new DBSearchUI(proxyClient, dbAccess, eventTriggers, asyncCalls);
        this.discogsUI = new DiscogsUI(proxyClient, dbAccess, eventTriggers, asyncCalls);
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
            if (tabTitle.equals("Discogs") && !sessionAuth) {
                asyncAuthCheck();
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
                            sessionAuth = true;
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

    private void asyncAuthCheck() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {

            @Override
            protected Boolean doInBackground() {
                return authorizationClient.hasAuthorization();
            } // doInBackground()

            @Override
            protected void done() {
                try {
                    if (get() == false) {
                        spawnAuthorizationInput();
                    }
                    else sessionAuth = true;
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            } // done()
        };
        worker.execute();
    } // asyncAuthCheck()

    private static void setDefaultThumbNail(URL url) {
        Image scaledDefaultThumbNail = new ImageIcon(url)
                .getImage().getScaledInstance(Constants.albumArtWidth, Constants.albumArtHeight, Image.SCALE_SMOOTH);
        Constants.defaultThumbNail =  new ImageIcon(scaledDefaultThumbNail);
    }

}
