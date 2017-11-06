package com.sidera.meetsfood.events;

import com.sidera.meetsfood.api.beans.ContabilitaRowV20;
import com.sidera.meetsfood.api.beans.Disdette;

import java.util.ArrayList;

/**
 * Created by kevin.vender on 21/09/2017.
 */

public class DisdettaInfoLoadedEvent {


    public ArrayList<Disdette> disdettaInfo;

    public DisdettaInfoLoadedEvent(ArrayList<Disdette> disdettaInfo) {
        this.disdettaInfo = disdettaInfo;
    }
}
