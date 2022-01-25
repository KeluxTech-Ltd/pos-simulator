/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jayrush.springmvcrest.domain;

/**
 * @author OlalekanW
 */
public class Response {

    private String respCode;
    private String respDescription;
    private Object respBody;

    /**
     * @return the respCode
     */
    public String getRespCode() {
        return respCode;
    }

    /**
     * @param respCode the respCode to set
     */
    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    /**
     * @return the respDescription
     */
    public String getRespDescription() {
        return respDescription;
    }

    /**
     * @param respDescription the respDescription to set
     */
    public void setRespDescription(String respDescription) {
        this.respDescription = respDescription;
    }

    public Object getRespBody() {
        return respBody;
    }

    public void setRespBody(Object respBody) {
        this.respBody = respBody;
    }

    @Override
    public String toString() {
        return "Response{" +
                "respCode='" + respCode + '\'' +
                ", respDescription='" + respDescription + '\'' +
                ", respBody=" + respBody +
                '}';
    }
}
