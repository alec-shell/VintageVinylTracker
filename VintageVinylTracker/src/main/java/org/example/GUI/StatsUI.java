package org.example.GUI;

import org.example.Client.ProxyClient;
import org.example.Configurable.Constants;
import org.example.Controller.DatabaseController;
import org.example.Controller.StatsController;
import org.example.DTO.CollectionStats;
import org.example.GUI.async.AsyncCalls;
import org.example.Repository.DatabaseRepository;
import org.example.DTO.Record;

import javax.swing.*;
import java.awt.*;

public class StatsUI extends JPanel {
    private final AsyncCalls asyncCalls;
    private CollectionStats collectionStats;
    private final DatabaseController databaseController;
    private final ProxyClient proxyClient;
    private JLabel statsLbl;
    private final JLabel mostValuableArtwork = new JLabel();
    private final JLabel leastValuableArtwork = new JLabel();
    private final JLabel leastValuableText = new JLabel("", JLabel.CENTER);
    private final JLabel mostValuableText = new JLabel("", JLabel.CENTER);

    public StatsUI(AsyncCalls asyncCalls, DatabaseController databaseController, ProxyClient proxyClient) {
        this.asyncCalls = asyncCalls;
        this.databaseController = databaseController;
        this.proxyClient = proxyClient;
        this.setLayout(new BorderLayout());
        this.setBackground(Constants.bgColor);
        this.collectionStats = StatsController.getStats(databaseController);
        buildPanel();
        if (collectionStats.getIsUpdating()) {
            updateStatsWorker();
        }
    } // constructor

    public void buildPanel() {
        this.add(buildAlbumsDisplay(), BorderLayout.NORTH);
        buildStatsDisplay();
        this.add(statsLbl, BorderLayout.CENTER);
    } // buildPanel()

    private JPanel buildAlbumsDisplay() {
        JPanel displayPanel = new JPanel();
        displayPanel.setBackground(Constants.bgColor);
        displayPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        mostValuableArtwork.setPreferredSize(new Dimension(Constants.albumArtWidth, Constants.albumArtHeight));
        leastValuableArtwork.setPreferredSize(new Dimension(Constants.albumArtWidth, Constants.albumArtHeight));
        String mostValUrl = collectionStats.getMostValuableRecord() != null ?
                collectionStats.getMostValuableRecord().getThumbUrl() : "";
        String leastValUrl = collectionStats.getLeastValuableRecord() != null ?
                collectionStats.getLeastValuableRecord().getThumbUrl() : "";
        asyncCalls.asyncThumbnailCall(mostValUrl, mostValuableArtwork, null, 0);
        asyncCalls.asyncThumbnailCall(leastValUrl, leastValuableArtwork, null, 0);
        displayPanel.add(buildAlbumContainer(collectionStats.getMostValuableRecord(),
                "Most Valuable: ",  mostValuableArtwork, mostValuableText),  c);
        c.gridx = 2;
        displayPanel.add(buildAlbumContainer(collectionStats.getLeastValuableRecord(),
                "Least Valuable: ", leastValuableArtwork, leastValuableText), c);
        return displayPanel;
    } // buildAlbumsDisplay()

    private JPanel buildAlbumContainer(Record record, String title, JLabel artwork, JLabel albumInfo) {
        JPanel container = new JPanel();
        container.setBackground(Constants.bgColor);
        container.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        container.setLayout(new BorderLayout());
        if (record != null) {
            String infoText = buildAlbumText(record);
            albumInfo.setText(infoText);
            container.add(albumInfo, BorderLayout.SOUTH);
        } else {
            container.add(albumInfo, BorderLayout.SOUTH);
        }
        container.add(new JLabel(title), BorderLayout.NORTH);
        container.add(artwork, BorderLayout.CENTER);
        return container;
    } // buildAlbumContainer()

    private void buildStatsDisplay() {
        statsLbl = new JLabel(buildStatsText(), JLabel.CENTER);
        statsLbl.setFont(new Font("Arial", Font.PLAIN, 24));
    } // buildStatsJTA()

    public void updateStatsUI() {
        collectionStats = StatsController.getStats(databaseController);
        Record mostValuableRecord = collectionStats.getMostValuableRecord();
        Record leastValuableRecord = collectionStats.getLeastValuableRecord();
        statsLbl.setText(buildStatsText());
        String mostValUrl = collectionStats.getMostValuableRecord() != null ?
                collectionStats.getMostValuableRecord().getThumbUrl() : "";
        String leastValUrl = collectionStats.getLeastValuableRecord() != null ?
                collectionStats.getLeastValuableRecord().getThumbUrl() : "";
        asyncCalls.asyncThumbnailCall(mostValUrl, mostValuableArtwork, null, 0);
        asyncCalls.asyncThumbnailCall(leastValUrl, leastValuableArtwork, null, 0);
        mostValuableText.setText(buildAlbumText(mostValuableRecord));
        leastValuableText.setText(buildAlbumText(leastValuableRecord));
    } // updateStatsUI()

    private String buildStatsText() {
        StringBuilder statsText = new StringBuilder();
        statsText.append("<html>");
        statsText.append("Total Albums Owned: ").append(collectionStats.getOwnedCount());
        statsText.append("<br>");
        statsText.append("\nTotal Invested: $").append(String.format("%.2f", collectionStats.getTotalInvested()));
        statsText.append("<br>");
        statsText.append("\nTotal Estimated Value: $").append(String.format("%.2f", collectionStats.getTotalValue()));
        statsText.append("</html>");
        return statsText.toString();
    } // buildStatsText()

    private String buildAlbumText(Record record) {
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
            return infoText.toString();
        }
        else return "";
    } // getAlbumText()

    private void updateStatsWorker() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                StatsController.updateOwnedValues(collectionStats.getOwnedRecords(), proxyClient, databaseController);
                return null;
            } // doInBackground()

            @Override
            protected void done() {
                updateStatsUI();
            } // done()
        };
        worker.execute();
    } // updateStats()

} // StatsUI
