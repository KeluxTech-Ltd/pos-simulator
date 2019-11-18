package com.jayrush.springmvcrest.exceptions;

public class tmsExceptions extends RuntimeException {

    public tmsExceptions() {
        super("Failed to perform the requested action");
    }

    public tmsExceptions(Throwable cause) {
        super("Failed to perform the requested action", cause);
    }

    public tmsExceptions(String message) {
        super(message);
    }

    public tmsExceptions(String message, Throwable cause) {
        super(message, cause);
    }
}
