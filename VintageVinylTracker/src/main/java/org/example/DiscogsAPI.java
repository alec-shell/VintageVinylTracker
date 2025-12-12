package org.example;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1RequestToken;

public class DiscogsAPI extends DefaultApi10a {

    protected DiscogsAPI() {
    } // constructor

    private static class InstanceHolder {
        private static final DiscogsAPI INSTANCE = new DiscogsAPI();
    } // InstanceHolder class

    public static  DiscogsAPI getInstance() {
        return InstanceHolder.INSTANCE;
    } // getInstance()

    @Override
    public String getRequestTokenEndpoint() {
        return "https://api.discogs.com/oauth/request_token";
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://api.discogs.com/oauth/access_token";
    }

    @Override
    protected String getAuthorizationBaseUrl(){
        return "https://www.discogs.com/oauth/authorize?oauth_token=%s";
    }

    @Override
    public String getAuthorizationUrl(OAuth1RequestToken requestToken) {
        return String.format(getAuthorizationBaseUrl(), requestToken.getToken());
    }
}
