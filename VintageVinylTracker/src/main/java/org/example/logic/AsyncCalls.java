package org.example.logic;

import org.example.client.ProxyClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class AsyncCalls {
    public static final int albumArtWidth = 180;
    public static final int albumArtHeight = 180;
    public final URL defaultURL = getClass().getResource("/img/defaultThumbnail.jpg");
    public Image scaledDefaultThumbNail = new ImageIcon(defaultURL)
            .getImage().getScaledInstance(albumArtWidth, albumArtHeight, Image.SCALE_SMOOTH);
    public final ImageIcon defaultThumbNail = new ImageIcon(scaledDefaultThumbNail);

    public void asyncPricingCall(ProxyClient proxyClient, int id, JLabel label) {
        SwingWorker<String, Void> worker = new SwingWorker<>() {

            @Override
            protected String doInBackground() {
                return ParseAPIResponse.buildPricingQueryCollection(proxyClient, id);
            } // doInBackground()

            @Override
            protected void done() {
                try {
                    label.setText(this.get());
                } catch (InterruptedException | ExecutionException e) {
                    label.setText("Unavailable");
                }
            } // done()
        };
        worker.execute();
    } // asyncPricingCall()

    public void asyncThumbnailCall(String thumbUrl, JLabel label) {
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
