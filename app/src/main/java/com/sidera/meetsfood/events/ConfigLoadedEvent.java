package com.sidera.meetsfood.events;

import com.sidera.meetsfood.api.beans.Configurazione;

import java.util.ArrayList;

public class ConfigLoadedEvent {

    public ArrayList<Configurazione> confCommessa;

    public ConfigLoadedEvent(ArrayList<Configurazione> confCommessa) {
        this.confCommessa = confCommessa;
    }

}
