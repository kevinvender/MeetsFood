package com.sidera.meetsfood.events;

/**
 * Created by Lorenzo on 13/06/2016.
 */
public class LoadListEvent {
    public String pagatore;

    public LoadListEvent(String pagatore) {
        this.pagatore = pagatore;
    }
}
