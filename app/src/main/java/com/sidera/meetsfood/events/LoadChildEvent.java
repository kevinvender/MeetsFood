package com.sidera.meetsfood.events;

/**
 * Created by Lorenzo on 13/06/2016.
 */
public class LoadChildEvent {
    public String pagatore;
    public String utenza;
    public String tipologia;

    public LoadChildEvent(String pagatore, String utenza, String tipologia) {
        this.pagatore = pagatore;
        this.utenza = utenza;
        this.tipologia = tipologia;
    }
}
