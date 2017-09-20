package com.sidera.meetsfood.events;

import com.sidera.meetsfood.api.beans.Contatti;

import java.util.ArrayList;

public class ContattiLoadedEvent {

    public ArrayList<Contatti> contatti;

    public ContattiLoadedEvent(ArrayList<Contatti> contatti) {
        this.contatti = contatti;
    }

}
