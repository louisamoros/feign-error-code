package com.example.feignerrorcode.exception;

public class ApiException extends Exception {

    public ApiException() {
        super();
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public String errorCode() {
        return "an error code";
    }
}
