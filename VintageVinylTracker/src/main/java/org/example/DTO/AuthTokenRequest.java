package org.example.DTO;

public class AuthTokenRequest {
    public String token;
    public String secret;
    public String verifier;

    public AuthTokenRequest() {}

    public AuthTokenRequest(String token, String secret, String verifier) {
        this.token = token;
        this.secret = secret;
        this.verifier = verifier;
    } // constructor

} // AuthTokenRequest class
