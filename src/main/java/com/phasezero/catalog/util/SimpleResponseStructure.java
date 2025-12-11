package com.phasezero.catalog.util;

public class SimpleResponseStructure {

    private int status;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static SimpleResponseStructure create(int status, String message) {

        SimpleResponseStructure response = new SimpleResponseStructure();
        response.setStatus(status);
        response.setMessage(message);

        return response;
    }
}