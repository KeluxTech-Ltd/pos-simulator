package com.jayrush.springmvcrest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jayrush.springmvcrest.serviceProviders.Models.serviceProviders;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String institutionID;
    private String institutionName;
    private String institutionEmail;
    private String institutionPhone;
    private String settlementAccount;
    private String createdBy;
    private String dateCreated;

    @OneToOne(cascade = CascadeType.ALL)
    private serviceProviders serviceProviders;

    private String bank;

    private boolean saved = false;
    private String savedDescription;
    private String institutionURL;
    private String institutionAppKey;
    private String institutionIntegrationVersion;



}
