package org.example.GUI;

import org.example.Config.Constants;
import org.example.GUI.async.AsyncCalls;
import org.example.Logic.GenerateStats;
import org.example.DTO.Record;

import javax.swing.*;
import java.awt.*;

public class StatsUI extends JPanel {
    private final GenerateStats collectionStats;
    private final AsyncCalls asyncCalls;

    public StatsUI(GenerateStats collectionStats, AsyncCalls asyncCalls) {
        this.collectionStats = collectionStats;
        this.asyncCalls = asyncCalls;
        this.setLayout(new BorderLayout());
        this.setBackground(Constants.bgColor);
        buildPanel();
        if (collectionStats.isUpdating()) {
            updateStats();
        }
    } // constructor

    public void buildPanel() {
        this.add(buildAlbumsDisplay(), BorderLayout.NORTH);
        this.add(buildStatsDisplay(), BorderLayout.CENTER);
    } // buildPanel()

    private JPanel buildAlbumsDisplay() {
        JPanel displayPanel = new JPanel();
        displayPanel.setBackground(Constants.bgColor);
        displayPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        JLabel mostValuableArtwork = new JLabel();
        JLabel leastValuableArtwork = new JLabel();
        mostValuableArtwork.setPreferredSize(new Dimension(Constants.albumArtWidth, Constants.albumArtHeight));
        leastValuableArtwork.setPreferredSize(new Dimension(Constants.albumArtWidth, Constants.albumArtHeight));
        String mostValUrl = collectionStats.getMostValuableRecord() != null ?
                collectionStats.getMostValuableRecord().getThumbUrl() : "";
        String leastValUrl = collectionStats.getLeastValuableRecord() != null ?
                collectionStats.getLeastValuableRecord().getThumbUrl() : "";
        asyncCalls.asyncThumbnailCall(mostValUrl, mostValuableArtwork);
        asyncCalls.asyncThumbnailCall(leastValUrl, leastValuableArtwork);
        displayPanel.add(buildAlbumContainer(collectionStats.getMostValuableRecord(), "Most Valuable: ",  mostValuableArtwork),  c);
        c.gridx = 2;
        displayPanel.add(buildAlbumContainer(collectionStats.getLeastValuableRecord(), "Least Valuable: ", leastValuableArtwork), c);
        return displayPanel;
    } // buildAlbumsDisplay()

    private JPanel buildAlbumContainer(Record record, String title, JLabel artwork) {
        JPanel container = new JPanel();
        container.setBackground(Constants.bgColor);
        container.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        container.setLayout(new BorderLayout());
        if (record != null) {
            StringBuilder infoText = new StringBuilder();
            infoText.append("<html>");
            infoText.append(record.getAlbumName());
            infoText.append("<br>");
            infoText.append(record.getArtistName());
            infoText.append("<br>");
            infoText.append("Purchase Price: $");
            infoText.append(String.format("%.2f", record.getPurchasePrice()));
            infoText.append("<br>");
            infoText.append("Current Value: $");
            infoText.append(String.format("%.2f", record.getValue()));
            infoText.append("</html>");
            JLabel albumInfo = new JLabel(infoText.toString(), JLabel.CENTER);
            container.add(albumInfo, BorderLayout.SOUTH);
        } else {
            JLabel albumInfo = new JLabel("",  JLabel.CENTER);
            container.add(albumInfo, BorderLayout.SOUTH);
        }
        container.add(new JLabel(title), BorderLayout.NORTH);
        container.add(artwork, BorderLayout.CENTER);
        return container;
    } // buildAlbumContainer()

    private JLabel buildStatsDisplay() {
        StringBuilder statsText = new StringBuilder();
        statsText.append("<html>");
        statsText.append("Total Albums Owned: ").append(collectionStats.getOwnedCount());
        statsText.append("<br>");
        statsText.append("\nTotal Invested: $").append(String.format("%.2f", collectionStats.getTotalInvested()));
        statsText.append("<br>");
        statsText.append("\nTotal Estimated Value: $").append(String.format("%.2f", collectionStats.getTotalValue()));
        statsText.append("</html>");
        JLabel statsLbl = new JLabel(statsText.toString(), JLabel.CENTER);
        statsLbl.setFont(new Font("Arial", Font.PLAIN, 24));
        return statsLbl;
    } // buildStatsJTA()

    private void updateStats() {
        SwingWorker<Object, Void> worker = new SwingWorker<>() {
            @Override
            protected Object doInBackground() throws Exception {
                collectionStats.updateOwnedValues();
                return null;
            } // doInBackgroun()

            @Override
            protected void done() {
                buildPanel();
            } // done()
        };
        worker.execute();
    } // updateStats()

} // StatsUI class
