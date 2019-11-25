package com.jayrush.springmvcrest.domain.domainDTO;

import lombok.Data;

@Data
public class PagedRequestDTO {
    private int size;
    private int page;
}
