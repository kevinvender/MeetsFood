package com.sidera.meetsfood.events;

/**
 * Created by Lorenzo on 13/06/2016.
 */
public class LoadEstrattoContoEvent {
    public String pagatore;
    public String utenza;
    public String tipologia;
    public String da;
    public String a;
    public String tipo_estrazione;

    public LoadEstrattoContoEvent(String pagatore, String utenza,String data_da,String data_a, String tipologia,String tipo_estrazione) {
        this.pagatore = pagatore;
        this.utenza = utenza;
        this.tipologia = tipologia;
        this.da = data_da;
        this.a = data_a;
        this.tipo_estrazione = tipo_estrazione;
    }
}
