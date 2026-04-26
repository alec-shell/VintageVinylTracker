package org.example.GUI.async;

import org.example.Client.ProxyClient;
import org.example.Config.Constants;
import org.example.Controller.APIController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class AsyncCalls {

    public void asyncPricingCall(ProxyClient proxyClient, int id, JLabel label, HashMap<String, Double> prices) {
        SwingWorker<HashMap<String, Double>, Void> worker = new SwingWorker<>() {

            @Override
            protected HashMap<String, Double> doInBackground() {
                return APIController.getPricingHashMap(proxyClient, id);
            } // doInBackground()

            @Override
            protected void done() {
                try {
                    // JLabel allows for html formatting
                    prices.clear();
                    prices.putAll(get());
                    String html = convertToHtml(prices);

                    label.setText(html);
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

    private String convertToHtml(HashMap<String, Double> prices) {
        StringBuilder returnString = new StringBuilder();
        returnString.append("<html>");
        for (String condition : Constants.pricingConditions) {
            returnString.append(condition);
            returnString.append(": ");
            returnString.append(String.format("%.2f", prices.get(condition)));
            returnString.append("<br>");
        }
        returnString.append("</html>");
        return returnString.toString();
    } // convertToHtml()

} // AsyncCalls class
