package com.sidera.meetsfood.events;

import com.sidera.meetsfood.api.beans.Menu;

import java.util.ArrayList;

public class MenuLoadedEvent {

    public ArrayList<Menu> menu;

    public MenuLoadedEvent(ArrayList<Menu> menu) {
        this.menu = menu;
    }

}
