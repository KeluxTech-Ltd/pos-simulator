package com.jayrush.springmvcrest.domain.domainDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class TransactionListDTO {
    private boolean hasNextRecord;
    private int totalCount;

    @JsonProperty("transactions")
    private List<TerminalTransactions> transactions = new ArrayList<>();
}
