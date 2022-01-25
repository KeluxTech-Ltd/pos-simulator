package com.jayrush.springmvcrest.exceptions;

/**
 * Created by ... on 24/11/2018.
 */
public class EmvProcessingException extends Exception {

    public EmvProcessingException(String message, Throwable e) {
        super(message, e);
    }

    public EmvProcessingException(String message) {
        super(message);
    }
}
