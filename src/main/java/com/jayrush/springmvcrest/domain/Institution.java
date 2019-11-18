package com.jayrush.springmvcrest.domain;

import lombok.Data;

import javax.persistence.*;
@Data
@Entity
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String institutionID;
    private String institutionName;
    private String institutionLocation;
    private String institutionEmail;
//    private String username;
//    private String password;
    private String institutionPhone;
    private String institutionAddress;
    private String merchantAccount;
    private String createdBy;
    private String dateCreated;
//    private String auth_token;
    private String processorIP;
    private String processorPort;
    private String processorName;
}
