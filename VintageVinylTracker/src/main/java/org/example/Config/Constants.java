package org.example.Config;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Constants {

    // Pricing conditions
    public static String[] pricingConditions = new String[] {
            "Mint (M)",
            "Near Mint (NM or M-)",
            "Very Good Plus (VG+)",
            "Very Good (VG)",
            "Good Plus (G+)",
            "Good (G)",
            "Fair (F)",
            "Poor (P)"
    };

     // JTable row labels
     public static final String[] dbColumnNames = new String[]{
             "Catalog No.",
             "Artist",
             "Album",
             "Year",
             "Country",
             "Owned",
             "Condition"
     };

    public static final String[] discogsColumnNames = {
            "Catalog No.",
            "Artist",
            "Album",
            "Year",
            "Country"
    };

    // colors
    public static final Color bgColor = Color.DARK_GRAY;

    // Dimensions
    public static final int albumArtWidth = 180;
    public static final int albumArtHeight = 180;

    // default icon
    public static final String defaultIconImgPath = "/img/defaultThumbnail.jpg";
    public static ImageIcon defaultThumbNail;

    // limits
    public static final int RESULTS_CAP = 250;

}
