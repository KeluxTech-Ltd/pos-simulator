package com.jayrush.springmvcrest.utility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Sohlowmawn on 26/11/2018.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "IccData")
public class IccData {

    @XmlElement(name = "IccRequest")
    private IccRequest iccRequest;
    @XmlElement(name = "IccResponse")
    private IccResponse iccResponse;

    public IccRequest getIccRequest() {
        return iccRequest;
    }

    public void setIccRequest(IccRequest iccRequest) {
        this.iccRequest = iccRequest;
    }

    public IccResponse getIccResponse() {
        return iccResponse;
    }

    public void setIccResponse(IccResponse iccResponse) {
        this.iccResponse = iccResponse;
    }

}
