package com.sidera.meetsfood.events;

import java.text.Normalizer;

/**
 * Created by Lorenzo on 13/06/2016.
 */
public class ChangePasswordEvent {
    public String requestString;

    public ChangePasswordEvent(String requestString) {
        this.requestString = requestString;
    }
}
