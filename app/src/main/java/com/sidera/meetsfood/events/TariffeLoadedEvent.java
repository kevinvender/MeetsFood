package com.sidera.meetsfood.events;



import com.sidera.meetsfood.api.beans.Tariffe;

import java.util.ArrayList;

/**
 * Created by kevin.vender on 21/09/2017.
 */

public class TariffeLoadedEvent {

    public ArrayList<Tariffe> tariffe;

    public TariffeLoadedEvent(ArrayList<Tariffe> tariffe) {
        this.tariffe = tariffe;
    }
}
