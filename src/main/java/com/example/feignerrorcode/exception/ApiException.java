package com.example.feignerrorcode.exception;

public class ApiException extends Exception {

    public ApiException() {
        super();
    }

    protected ApiException(String message) {
        super(message);
    }

    protected ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public String errorCode() {
        return "an error code";
    }
}
