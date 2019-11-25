package com.jayrush.springmvcrest.domain.domainDTO;

import lombok.Data;

@Data
public class PagedInstitutionRequestDTO {
    private Long institutionID;
    private int size;
    private int page;
}
