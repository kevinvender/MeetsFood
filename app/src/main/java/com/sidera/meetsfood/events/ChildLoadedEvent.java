package com.sidera.meetsfood.events;

import com.sidera.meetsfood.api.beans.FiglioDettagli;
import com.sidera.meetsfood.api.beans.FiglioTestata;

import java.util.ArrayList;

public class ChildLoadedEvent {

    public FiglioDettagli figlioDettagli;

    public ChildLoadedEvent(FiglioDettagli figlioDettagli) {
        this.figlioDettagli = figlioDettagli;
    }

}
