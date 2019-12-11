package com.jayrush.springmvcrest.fep;


/**
 * Created by ... on 24/11/2018.
 */
public class RequestProcessingException extends Exception {

    private ErrorResponse error;

    public RequestProcessingException(String message, Throwable e) {
        super(message, e);
    }

    public RequestProcessingException(String message) {
        super(message);
        this.error = ErrorResponse.fromCodeAndMessage(ResponseCode.ERROR, message);
    }

    public RequestProcessingException(String message, String code) {
        super(message);
        this.error = ErrorResponse.fromCodeAndMessage(code, message);
    }

    public RequestProcessingException(String message, String code, Throwable e) {
        super(message, e);
        this.error = ErrorResponse.fromCodeAndMessage(code, message);
    }

    public ErrorResponse getError() {
        return this.error;
    }
}
