package org.example.client;

import com.github.javakeyring.Keyring;

import java.net.http.HttpClient;

public class DiscogsClient {
    private final HttpClient httpClient;
    private final Keyring keyring;


    public DiscogsClient(HttpClient httpClient, Keyring keyring) {
        this.httpClient = httpClient;
        this.keyring = keyring;
    } // constructor


} // DiscogsClient class
