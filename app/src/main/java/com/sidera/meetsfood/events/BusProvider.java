package com.sidera.meetsfood.events;

import com.squareup.otto.Bus;

/**
 * Created by Lorenzo on 13/06/2016.
 */
public class BusProvider {
    private static Bus bus;

    public static Bus getInstance() {
        if (bus != null)
            return bus;
        else {
            bus = new Bus();
            return bus;
        }
    }
}
