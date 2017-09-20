package com.sidera.meetsfood.events;

import com.sidera.meetsfood.api.beans.ContabilitaRow;

import java.util.ArrayList;

public class PresenzeLoadedEvent {

    public ArrayList<ContabilitaRow> presenze;

    public PresenzeLoadedEvent(ArrayList<ContabilitaRow> presenze) {
        this.presenze = presenze;
    }

}
