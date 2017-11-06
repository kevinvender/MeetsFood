package com.sidera.meetsfood.events;

import com.sidera.meetsfood.api.beans.ContabilitaRow;
import com.sidera.meetsfood.api.beans.ContabilitaRowV20;

import java.util.ArrayList;

public class PresenzeLoadedEvent {

    public ArrayList<ContabilitaRowV20> presenze;

    public PresenzeLoadedEvent(ArrayList<ContabilitaRowV20> presenze) {
        this.presenze = presenze;
    }

}
