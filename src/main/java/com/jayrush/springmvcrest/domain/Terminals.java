package com.jayrush.springmvcrest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jayrush.springmvcrest.serviceProviders.Models.profiles;
import com.jayrush.springmvcrest.serviceProviders.Models.serviceProviders;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "terminals")
public class Terminals {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Generates the ID for us
    private long id;
    private String terminalID;
    private String TerminalType;
    private String TerminalSerialNo;
    private String TerminalROMVersion;
    private String dateCreated;
    private String savedDescription;
    @JsonIgnore
    private boolean isSaved;

    @OneToOne
    private profiles profile;
    @ManyToOne
    private Institution institution;
}
