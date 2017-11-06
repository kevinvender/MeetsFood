package com.sidera.meetsfood.events;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kevin.vender on 21/09/2017.
 */

public class LoadDisdettaInfoEvent {

    public String commessa;
    public String utenza;
    public String data;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public LoadDisdettaInfoEvent(String commessa, String utenza,Date dataM) {
        this.commessa = commessa;
        this.utenza = utenza;
        this.data = sdf.format(dataM);
    }
}
