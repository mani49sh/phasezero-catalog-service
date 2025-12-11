package com.phasezero.catalog.exception;

public class DuplicatePartNumberException extends BaseException {

    public DuplicatePartNumberException(String message) {
        super(message, "DUPLICATE_PART_NUMBER", 409);
    }
}