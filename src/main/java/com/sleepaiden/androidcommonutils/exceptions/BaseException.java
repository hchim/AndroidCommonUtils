package com.sleepaiden.androidcommonutils.exceptions;

public class BaseException extends Exception {
    String errorCode;
    
    public BaseException(String errorCode) {
        this.errorCode = errorCode;
    }

    public BaseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
