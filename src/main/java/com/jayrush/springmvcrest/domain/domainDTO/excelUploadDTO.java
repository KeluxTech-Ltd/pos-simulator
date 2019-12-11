package com.jayrush.springmvcrest.domain.domainDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author JoshuaO
 */

@Data
public class excelUploadDTO {
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
