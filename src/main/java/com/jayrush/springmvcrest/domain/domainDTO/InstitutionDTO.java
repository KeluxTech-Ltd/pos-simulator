package com.jayrush.springmvcrest.domain.domainDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class InstitutionDTO {
    @JsonIgnore
    private long id;
    private String institutionName;
    private String institutionEmail;
    private String institutionPhone;
    private String settlementAccount;
    private String createdBy;
    private String dateCreated;
    private String serviceProviderName;
    private String bank;
}