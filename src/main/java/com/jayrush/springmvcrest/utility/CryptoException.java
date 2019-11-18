package com.jayrush.springmvcrest.utility;

/**
 * Created by ... on 24/11/2018.
 */
public class CryptoException extends Exception {

    public CryptoException(String message, Throwable e) {
        super(message, e);
    }

    public CryptoException(String message) {
        super(message);
    }
}
