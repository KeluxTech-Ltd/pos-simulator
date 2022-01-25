package com.jayrush.springmvcrest.domain.domainDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class InstitutionDTO {
    private String institutionName;
    private String institutionEmail;
    private String institutionPhone;
    private String settlementAccount;
    private String createdBy;
    private String dateCreated;
    private String serviceProviderName;
    private String bank;
    private String institutionURL;
    private String institutionAppKey;
    private String institutionIntegrationVersion;
    private Double minimumCharge;
    private Double maximumCharge;
    private Double feePercentage;
    private String token;
    private Boolean globalSettings;
}
