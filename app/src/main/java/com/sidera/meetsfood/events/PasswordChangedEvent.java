package com.sidera.meetsfood.events;

import android.util.Log;

import com.sidera.meetsfood.api.beans.ResponseString;

/**
 * Created by Lorenzo on 13/06/2016.
 */
public class PasswordChangedEvent {
    public ResponseString responseString;

    public PasswordChangedEvent(ResponseString responseString) {
        Log.e("PASSWORD","PasswordChangedEvent");
        this.responseString = responseString;
    }
}
