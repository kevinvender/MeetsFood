package com.sidera.meetsfood.events;

import com.sidera.meetsfood.api.beans.ContabilitaRow;
import com.sidera.meetsfood.api.beans.ContabilitaRowV20;
import com.sidera.meetsfood.api.beans.FiglioDettagli;

import java.util.ArrayList;

public class EstrattoContoLoadedEvent {

    public ArrayList<ContabilitaRowV20> conto;

    public EstrattoContoLoadedEvent(ArrayList<ContabilitaRowV20> conto) {
        this.conto = conto;
    }

}
