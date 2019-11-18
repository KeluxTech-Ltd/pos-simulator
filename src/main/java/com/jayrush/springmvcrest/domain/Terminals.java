package com.jayrush.springmvcrest.domain;

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
    private String TerminalStatus;
    private String dateCreated;
    @ManyToOne
    private Institution institution;
}
