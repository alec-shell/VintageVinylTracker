package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class AsyncCalls {
    public static final int albumArtWidth = 180;
    public static final int albumArtHeight = 180;
    public static final Image scaledDefaultThumbNail = new ImageIcon("VintageVinylTracker/img/defaultThumbnail.jpg")
            .getImage().getScaledInstance(albumArtWidth, albumArtHeight, Image.SCALE_SMOOTH);
    public static final ImageIcon defaultThumbNail = new ImageIcon(scaledDefaultThumbNail);

    public static void asyncPricingCall(int id, JLabel label, DiscogsAuthorization discogsAuth) {
        SwingWorker<String, Void> worker = new SwingWorker<>() {

            @Override
            protected String doInBackground() {
                return ParseAPIResponse.buildPricingQueryCollection(discogsAuth, id);
            } // doInBackground()

            @Override
            protected void done() {
                try {
                    label.setText(this.get().toString());
                } catch (InterruptedException | ExecutionException e) {
                    label.setText("Unavailable");
                }
            } // done()
        };
        worker.execute();
    } // asyncPricingCall()

    public static void asyncThumbnailCall(String thumbUrl, JLabel label) {
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                Image newImg = ImageIO.read(new URL(thumbUrl))
                        .getScaledInstance(albumArtWidth, albumArtHeight, Image.SCALE_DEFAULT);
                return new ImageIcon(newImg);
            } // doInBackground()

            @Override
            protected void done() {
                try {
                    label.setIcon((ImageIcon) get());
                } catch (InterruptedException | ExecutionException e) {
                    label.setIcon(defaultThumbNail);
                }
            } // done()
        };
        worker.execute();
    } // asyncThumbnailCall()

} // AsyncCalls class
