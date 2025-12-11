package com.phasezero.catalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateProductException extends RuntimeException {
    public DuplicateProductException(String partNumber) {
        super("Product with part number '" + partNumber + "' already exists");
    }
}