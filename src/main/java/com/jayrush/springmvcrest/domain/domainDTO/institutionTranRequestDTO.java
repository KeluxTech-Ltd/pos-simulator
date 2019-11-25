package com.jayrush.springmvcrest.domain.domainDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author JoshuaO
 */
@Data
public class institutionTranRequestDTO {
    private String authToken;
    @JsonIgnore
    private String adminName;
}
