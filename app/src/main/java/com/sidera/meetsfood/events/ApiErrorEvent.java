package com.sidera.meetsfood.events;


public class ApiErrorEvent {

    public Throwable error;

    public ApiErrorEvent(Throwable error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return error.getMessage();
    }
}
