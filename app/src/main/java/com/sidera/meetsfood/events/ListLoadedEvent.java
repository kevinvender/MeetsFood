package com.sidera.meetsfood.events;

import com.sidera.meetsfood.api.beans.Dummy;
import com.sidera.meetsfood.api.beans.FiglioTestata;

import java.util.ArrayList;

import retrofit2.Call;

public class ListLoadedEvent {

    public ArrayList<FiglioTestata> list;

    public ListLoadedEvent(ArrayList<FiglioTestata> list) {
        this.list = list;
    }

}
