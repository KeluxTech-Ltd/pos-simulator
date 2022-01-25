package com.jayrush.springmvcrest.domain.domainDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.Terminals;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TerminalListDTO {
    private boolean hasNextRecord;
    private int totalCount;

    @JsonProperty("transactions")
    private List<Terminals> terminals = new ArrayList<>();
}
