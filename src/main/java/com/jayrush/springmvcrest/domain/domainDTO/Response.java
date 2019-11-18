package com.jayrush.springmvcrest.domain.domainDTO;

import lombok.Data;

@Data
public class Response {
    private byte[] responseByte;
    private Object responseMsg;

}
