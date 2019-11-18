package com.jayrush.springmvcrest.domain.domainDTO;

import lombok.Data;

import javax.persistence.*;


@Data
public class TerminalTransactionsDTO {
    private long id;
    private String mti;
    private String pan;
    private String amount;
    private String dateTime;
    private String stan;
    private String time;
    private String date;
    private String responseCode;
    private String responseDesc;
    private String TerminalID;
    private String agentLocation;
    private String dateCreated;
}
