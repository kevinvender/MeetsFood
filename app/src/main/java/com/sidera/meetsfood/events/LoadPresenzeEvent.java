package com.sidera.meetsfood.events;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Lorenzo on 13/06/2016.
 */
public class LoadPresenzeEvent {
    public String da;
    public String a;
    public String utenza;
    public String tipologia;
   // public String tipo_estrazione;

    public LoadPresenzeEvent(String utenza, String tipologia, Date mese) {
        this.utenza = utenza;
        Calendar cal = Calendar.getInstance();
        cal.setTime(mese);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date da = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date a = cal.getTime();

        this.da = new SimpleDateFormat("yyyy-MM-dd").format(da);
        this.a = new SimpleDateFormat("yyyy-MM-dd").format(a);
        this.tipologia = tipologia;
        //this.tipo_estrazione = "0";
    }
}
