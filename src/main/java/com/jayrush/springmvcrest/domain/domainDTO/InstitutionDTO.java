package com.jayrush.springmvcrest.domain.domainDTO;

import lombok.Data;

@Data
public class InstitutionDTO {
    private long id;
    private String institutionID;
    private String institutionName;
    private String institutionLocation;
    private String institutionEmail;
    private String username;
    private String password;
    private String institutionPhone;
    private String institutionAddress;
    private String merchantAccount;
    private String createdBy;
    private String dateCreated;
    private String authToken;
    private String processorIP;
    private String processorPort;
    private String processorName;



}
