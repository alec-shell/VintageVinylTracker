package org.example.DTO;

public class SearchRequest {
    public String token;
    public String secret;
    public String artist;
    public String album;
    public String year;
    public String catNo;
    public int pageNo;

    public SearchRequest() {}

    public SearchRequest(String token, String secret, String artist, String album, String year, String catNo, int pageNo) {
        this.token = token;
        this.secret = secret;
        this.artist = artist;
        this.album = album;
        this.year = year;
        this.catNo = catNo;
        this.pageNo = pageNo;
    }

} // SearchRequest class
