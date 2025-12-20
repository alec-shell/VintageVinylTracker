package org.example;

import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class DiscogsClient {

    protected static String searchQuery(DiscogsAuthorization auth, String album, String artist, String year, String catNo, int pageNo) throws IOException, ExecutionException, InterruptedException {
        StringBuilder query = new StringBuilder();
        query.append("https://api.discogs.com/database/search?format=Vinyl&page=");
        query.append(pageNo);
        if  (album != null) query.append("&release_title=").append(album.strip().replace(" ", "+"));
        if (artist != null) query.append("&artist=").append(artist.strip().replace(" ", "+"));
        if (year != null) query.append("&year=").append(year.strip());
        if (catNo != null) query.append("&catno=").append(catNo.strip().replace(" ", "+"));

        OAuthRequest request =  new OAuthRequest(Verb.GET, query.toString());
        auth.getService().signRequest(auth.getAccessToken(), request);
        return auth.getService().execute(request).getBody();
    } // searchQuery()

    protected static String getPriceSuggestions(DiscogsAuthorization auth,int id) throws IOException, ExecutionException, InterruptedException {
        StringBuilder query = new StringBuilder();
        query.append("https://api.discogs.com/marketplace/price_suggestions/");
        query.append(id);
        OAuthRequest request =  new OAuthRequest(Verb.GET, query.toString());
        auth.getService().signRequest(auth.getAccessToken(), request);
        return auth.getService().execute(request).getBody();
    } // getPriceSuggestion()

} // DiscogsClient class
