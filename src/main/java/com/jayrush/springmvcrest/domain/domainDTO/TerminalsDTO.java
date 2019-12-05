package com.jayrush.springmvcrest.domain.domainDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Data
public class TerminalsDTO {
    @JsonIgnore
    private long id;
    private String terminalID;
    private String TerminalType;
    private String TerminalSerialNo;
    private String TerminalROMVersion;
    private String dateCreated;
    private String institutionID;
    private String profileName;
}
