/**
 * TrackerUI.java: GUI for VintageVinylTracker
 * alec-shell
 * 11/25/2025
 */

package org.example;

import javax.swing.*;
import java.awt.*;


public class MainUI extends JFrame {
    private final DBAccess dbAccess;
    private JTabbedPane tabsPane;
    private final DBSearchUI dbSearchUI;
    private final DiscogsUI webSearchUI;


    public MainUI() {
        this.setTitle("Vintage Vinyl Tracker");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLayout(new BorderLayout());
        this.dbAccess = new DBAccess();
        this.dbSearchUI = new DBSearchUI(dbAccess);
        this.webSearchUI = new DiscogsUI();
        buildTabbedPane();
        this.add(tabsPane, BorderLayout.CENTER);
    } // constructor


    private void buildTabbedPane() {
        tabsPane = new JTabbedPane(JTabbedPane.LEFT);
        tabsPane.add("Search Database",  dbSearchUI);
        tabsPane.add("Discogs", webSearchUI);
    } // buildTabbedPane()


    public static void main(String[] args) {
        JFrame frame = new MainUI();
        frame.setVisible(true);
    } // main()

} // TrackerUI class
