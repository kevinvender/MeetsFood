package com.sidera.meetsfood.events;

import com.sidera.meetsfood.api.beans.ContabilitaRow;
import com.sidera.meetsfood.api.beans.FiglioDettagli;

import java.util.ArrayList;

public class EstrattoContoLoadedEvent {

    public ArrayList<ContabilitaRow> conto;

    public EstrattoContoLoadedEvent(ArrayList<ContabilitaRow> conto) {
        this.conto = conto;
    }

}
