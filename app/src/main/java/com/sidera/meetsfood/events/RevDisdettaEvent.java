package com.sidera.meetsfood.events;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kevin.vender on 22/09/2017.
 */

public class RevDisdettaEvent {

    public String commessa;
    public String utenza;
    public String data;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public RevDisdettaEvent(String commessa, String utenza,Date dataM) {
        this.commessa = commessa;
        this.utenza = utenza;
        this.data = sdf.format(dataM);
    }
}
