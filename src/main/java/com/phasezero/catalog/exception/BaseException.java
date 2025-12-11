package com.phasezero.catalog.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
    private final String errorCode;
    private final int statusCode;

    public BaseException(String message, String errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
}