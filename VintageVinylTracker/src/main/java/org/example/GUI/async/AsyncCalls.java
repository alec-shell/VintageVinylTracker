package org.example.GUI.async;

import org.example.Client.ProxyClient;
import org.example.Config.Constants;
import org.example.Logic.ParseAPIResponse;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class AsyncCalls {

    public void asyncPricingCall(ProxyClient proxyClient, int id, JLabel label, ArrayList<Double> prices) {
        SwingWorker<HashMap<Double, String>, Void> worker = new SwingWorker<>() {

            @Override
            protected HashMap<Double, String> doInBackground() {
                return ParseAPIResponse.buildPricingQueryCollection(proxyClient, id);
            } // doInBackground()

            @Override
            protected void done() {
                try {
                    // JLabel allows for html formatting
                    HashMap<Double, String> result = get();
                    String html = convertToHtml(result.values());
                    prices.addAll(result.keySet());
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

    private String convertToHtml(Collection<String> pricingByCondition) {
        StringBuilder returnString = new StringBuilder();
        returnString.append("<html>");
        for (String price: pricingByCondition) {
            returnString.append(price);
            returnString.append("<br>");
        }
        returnString.append("</html>");
        return returnString.toString();
    } // convertToHtml()

} // AsyncCalls class
