package com.sidera.meetsfood.events;

import android.util.Log;

import com.sidera.meetsfood.api.beans.Disdette;
import com.sidera.meetsfood.api.beans.ResponseString;

import java.util.ArrayList;

/**
 * Created by kevin.vender on 22/09/2017.
 */

public class PastoInBiancoSettedEvent {

    public Disdette disdetta;

    public PastoInBiancoSettedEvent(Disdette disdetta) {
        Log.e("PASTO IN BIANCO","Pasto in bianco setted");
        this.disdetta = disdetta;
    }
}
