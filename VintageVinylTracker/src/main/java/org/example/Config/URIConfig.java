package org.example.Config;

import java.net.URI;

public class URIConfig {

    public static URI REQUEST_TOKEN_URI = URI.create("https://sowhatnow.us:8443/auth/r-tkn");
    public static URI REQUEST_VERIFIER_URI = URI.create("https://sowhatnow.us:8443/auth/url");
    public static URI AUTH_TOKEN_URI = URI.create("https://sowhatnow.us:8443/auth/a-tkn");
    public static URI AUTH_VERIFIER_URI = URI.create("https://sowhatnow.us:8443/discogs/identity");
    public static URI SEARCH_URI = URI.create("https://sowhatnow.us:8443/discogs/search");
    public static URI PRICING_URI = URI.create("https://sowhatnow.us:8443/discogs/pricing");

    // db access
    public static final String DB_URI =  "jdbc:sqlite:VintageVinyl.db";

    // default image src
    public static final String defaultIconImgPath = "/img/defaultThumbnail.jpg";
} // URIConfig class
