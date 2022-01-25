package com.jayrush.springmvcrest.domain.domainDTO;

import lombok.Data;

/**
 * @author JoshuaO
 */
@Data
public class uploadResponseDTO {
    private String terminalID;
    private boolean savedStatus;
    private String statusDescription;
}

