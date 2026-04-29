package org.example.DTO;

public class URLRequest {
    public String token;
    public String secret;

    public URLRequest(){}

    public URLRequest(String token, String secret) {
        this.token = token;
        this.secret = secret;
    }

} // URLRequest
