package com.jayrush.springmvcrest.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "transactionLogs")
public class TerminalTransactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String mti;
    private String pan;
    private String amount;
    private String dateTime;
    private String stan;
    private String time;
    private String date;
    private String rrn;
    private String responseCode;
    private String responseDesc;
    private String terminalID;
    private String agentLocation;
    private String dateCreated;
    private String institutionID;
    private String status;
}
