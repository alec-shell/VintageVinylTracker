package org.example.Logic;

import org.example.Client.ProxyClient;
import org.example.Config.Constants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class AsyncCalls {

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
                        .getScaledInstance(Constants.albumArtWidth, Constants.albumArtHeight, Image.SCALE_SMOOTH);
                return new ImageIcon(newImg);
            } // doInBackground()

            @Override
            protected void done() {
                try {
                    label.setIcon(get());
                } catch (InterruptedException | ExecutionException e) {
                    label.setIcon(Constants.defaultThumbNail);
                }
            } // done()
        };
        worker.execute();
    } // asyncThumbnailCall()

} // AsyncCalls class
