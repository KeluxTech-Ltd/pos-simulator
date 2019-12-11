package com.jayrush.springmvcrest.fep;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by ... on 24/11/2018.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ErrorResponse {
    private String responseCode;
    private String message;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ErrorResponse fromCodeAndMessage(String code, String message) {
        ErrorResponse response = new ErrorResponse();
        response.setResponseCode(code);
        response.setMessage(message);

        return response;
    }
}
