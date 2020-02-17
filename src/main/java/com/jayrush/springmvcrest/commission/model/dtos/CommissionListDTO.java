package com.jayrush.springmvcrest.commission.model.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jayrush.springmvcrest.commission.model.commission;
import com.jayrush.springmvcrest.wallet.models.walletTransaction;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommissionListDTO {
    private boolean hasNextRecord;
    private int totalCount;

    @JsonProperty("transactions")
    private List<commission> commissionTransactions = new ArrayList<>();
}
