package com.sidera.meetsfood.events;


public class LoadNewsEvent {
    public String commessa;
    public String utenza;

    public LoadNewsEvent(String commessa,String utenza) {
        this.commessa = commessa;
        this.utenza = utenza;
    }
}
