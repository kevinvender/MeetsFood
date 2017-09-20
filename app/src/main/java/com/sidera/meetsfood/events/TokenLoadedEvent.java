package com.sidera.meetsfood.events;

/**
 * Created by Lorenzo on 13/06/2016.
 */
public class TokenLoadedEvent {
    public String token;

    public TokenLoadedEvent(String token) {
        token = token;
    }
}
