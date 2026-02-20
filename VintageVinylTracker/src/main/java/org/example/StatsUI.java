package org.example;

import javax.swing.*;
import java.awt.*;

public class StatsUI extends JPanel {
    private GenerateStats collectionStats;


    public StatsUI(GenerateStats collectionStats) {
        this.collectionStats = collectionStats;
        this.setLayout(new BorderLayout());
        this.setBackground(Color.LIGHT_GRAY);
        buildPanel();
    } // constructor

    public void buildPanel() {
        this.add(buildAlbumsDisplay(), BorderLayout.NORTH);
        this.add(buildStatsDisplay(), BorderLayout.CENTER);
    } // buildPanel()

    private JPanel buildAlbumsDisplay() {
        JPanel displayPanel = new JPanel();
        displayPanel.setBackground(Color.LIGHT_GRAY);
        displayPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        JLabel mostValuableArtwork =  new JLabel();
        JLabel leastValuableArtwork =  new JLabel();
        mostValuableArtwork.setPreferredSize(new Dimension(AsyncCalls.albumArtWidth, AsyncCalls.albumArtHeight));
        leastValuableArtwork.setPreferredSize(new Dimension(AsyncCalls.albumArtWidth, AsyncCalls.albumArtHeight));
        AsyncCalls.asyncThumbnailCall(collectionStats.getMostValuableRecord().getThumbUrl(), mostValuableArtwork);
        AsyncCalls.asyncThumbnailCall(collectionStats.getLeastValuableRecord().getThumbUrl(), leastValuableArtwork);
        displayPanel.add(buildAlbumContainer(collectionStats.getMostValuableRecord(), "Most Valuable: ",  mostValuableArtwork),  c);
        c.gridx = 2;
        displayPanel.add(buildAlbumContainer(collectionStats.getLeastValuableRecord(), "Least Valuable: ", leastValuableArtwork), c);
        return displayPanel;
    } // buildAlbumsDisplay()

    private JPanel buildAlbumContainer(Record record, String title, JLabel artwork) {
        JPanel container = new JPanel();
        container.setBackground(Color.LIGHT_GRAY);
        container.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        container.setLayout(new BorderLayout());
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
        container.add(new JLabel(title), BorderLayout.NORTH);
        container.add(artwork, BorderLayout.CENTER);
        container.add(albumInfo, BorderLayout.SOUTH);
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
        return new JLabel(statsText.toString(), JLabel.CENTER);
    } // buildStatsJTA()

} // StatsUI class


/*
 * - make API call to Discogs, (60 / min rate limit).
 * - update DB entry with new value.
 * - store pending total value.
 * When ASYNC operation is done, update collection value total.
 * ** Need to track update date, limiting transaction to one per day. Stored in external file. **
 */
