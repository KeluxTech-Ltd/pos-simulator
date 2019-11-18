package com.jayrush.springmvcrest.domain.domainDTO;

import lombok.Data;

import javax.persistence.*;

@Data

public class TerminalsDTO {
    private long id;
    private String TerminalID;
    private String TerminalType;
    private String TerminalSerialNo;
    private String TerminalROMVersion;
    private String TerminalStatus;
    private String dateCreated;
}
