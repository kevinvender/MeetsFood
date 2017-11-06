package com.sidera.meetsfood.events;

import android.util.Log;

import com.sidera.meetsfood.api.beans.Disdette;
import com.sidera.meetsfood.api.beans.ResponseString;

import java.util.ArrayList;

/**
 * Created by kevin.vender on 22/09/2017.
 */

public class RevDisdettaSettedEvent {

    public Disdette disdetta;

    public RevDisdettaSettedEvent(Disdette disdetta) {
        Log.e("REV DISDETTA","Disdetta revocata");
        this.disdetta = disdetta;
    }
}
