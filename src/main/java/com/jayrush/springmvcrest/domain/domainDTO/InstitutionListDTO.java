package com.jayrush.springmvcrest.domain.domainDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.Terminals;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InstitutionListDTO {
    private boolean hasNextRecord;
    private int totalCount;

    @JsonProperty("transactions")
    private List<Institution> terminals = new ArrayList<>();
}
