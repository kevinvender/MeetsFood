package com.sidera.meetsfood.api.exceptions;


public class Error4xxException extends Exception {
    public Integer errorCode;

    public Error4xxException(Integer errorCode) {
        super(String.valueOf(errorCode));
        this.errorCode = errorCode;
    };
}
