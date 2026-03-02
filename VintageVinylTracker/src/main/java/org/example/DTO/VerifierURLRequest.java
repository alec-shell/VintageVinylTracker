package org.example.DTO;

public class VerifierURLRequest {
    public String token;
    public String secret;

    public VerifierURLRequest(){}

    public VerifierURLRequest(String token, String secret) {
        this.token = token;
        this.secret = secret;
    }

} // VerifierURLRequest class
