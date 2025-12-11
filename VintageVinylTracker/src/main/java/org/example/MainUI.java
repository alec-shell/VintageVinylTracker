/**
 * TrackerUI.java: GUI for VintageVinylTracker
 * alec-shell
 * 11/25/2025
 */

package org.example;

import javax.swing.*;
import java.awt.*;


public class TrackerUI extends JFrame {
    private JTabbedPane tabsPane;
    private JPanel ownedTab;
    private JPanel wantedTab;
    private JPanel searchDBTab;


    public TrackerUI() {
        this.setTitle("Vintage Vinyl Tracker");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLayout(new BorderLayout());
        buildTabbedPane();
        this.add(tabsPane, BorderLayout.CENTER);
    } // constructor


    private void buildTabbedPane() {
        tabsPane = new JTabbedPane(JTabbedPane.LEFT);
        buildOwnedTab();
        buildWantedTab();
        tabsPane.add("Owned", ownedTab);
        tabsPane.add("Wanted", wantedTab);
        tabsPane.add("Search Database", new DBSearchTabUI());
    } // buildTabbedPane()


    private void buildOwnedTab() {
        ownedTab = new JPanel();
        ownedTab.setBackground(Color.LIGHT_GRAY);
    } // ownedTab()


    private void buildWantedTab() {
        wantedTab = new JPanel();
        wantedTab.setBackground(Color.LIGHT_GRAY);
    } // wantedTab()



    public static void main(String[] args) {
        JFrame frame = new TrackerUI();
        frame.setVisible(true);
    } // main()


} // TrackerUI class
