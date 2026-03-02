package org.example.client;

import java.net.URI;

public class URIConfig {

    public static URI REQUEST_TOKEN_URI = URI.create("http://localhost:8080/auth/r-tkn");
    public static URI REQUEST_VERIFIER_URI = URI.create("http://localhost:8080/auth/url");
    public static URI AUTH_TOKEN_URI = URI.create("http://localhost:8080/auth/a-tkn");

} // URIConfig class
