package com.sidera.meetsfood.events;


import java.text.SimpleDateFormat;
import java.util.Date;

public class LoadMenuEvent {
    public String commessa;
    public String utenza;
    public String data;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public LoadMenuEvent(String commessa, String utenza,Date dataM) {
        this.commessa = commessa;
        this.utenza = utenza;
        this.data = sdf.format(dataM);
    }
}
