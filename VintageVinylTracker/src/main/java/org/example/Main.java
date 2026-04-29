/**
 * TrackerUI.java: GUI for VintageVinylTracker
 * alec-shell
 * 11/25/2025
 */

package org.example;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.formdev.flatlaf.FlatDarkLaf;
import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
import org.example.Client.AuthorizationClient;
import org.example.Client.ProxyClient;
import org.example.Configurable.URICollection;
import org.example.GUI.MainUI;
import org.example.GUI.async.AsyncCalls;
import org.example.Repository.DatabaseRepository;
import org.example.GUI.statsUpdate.EventTriggers;
import org.example.Infrastructure.database.DatabaseInitializer;
import org.example.Infrastructure.database.ConnectionFactory;

import javax.swing.*;
import java.awt.*;
import java.net.http.HttpClient;
import java.sql.Connection;
import java.time.Duration;


public class Main {
    AuthorizationClient authorizationClient;

    public Main() {
        HttpClient httpClient = HttpClient
                .newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
        Keyring keyRing;
        try {
            keyRing = Keyring.create();
        } catch (BackendNotSupportedException e) {
            throw new RuntimeException(e);
        }
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            UIManager.put("Label.font", new Font("Arial", Font.BOLD, 13));
        } catch (UnsupportedLookAndFeelException e) {
            System.out.println("Could not set look and feel");
        }
        JsonMapper mapper = new JsonMapper();
        this.authorizationClient = new AuthorizationClient(httpClient, keyRing, mapper);
        ProxyClient proxyClient = new ProxyClient(httpClient, keyRing, mapper);
        Connection conn;
        conn = ConnectionFactory.initConnection(URICollection.DB_URI);
        DatabaseInitializer.initTables(conn);
        DatabaseRepository dbAccess = new DatabaseRepository(conn);
        AsyncCalls asyncCalls = new AsyncCalls();
        EventTriggers eventTriggers = new EventTriggers();
        MainUI mainUI = new MainUI(proxyClient, dbAccess,
                authorizationClient, asyncCalls, eventTriggers);
        mainUI.setVisible(true);
    } // constructor


    public static void main(String[] args) {
        Main main = new Main();
    } // main()
        
} // Main
