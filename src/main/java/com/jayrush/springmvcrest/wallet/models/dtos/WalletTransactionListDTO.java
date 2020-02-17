package com.jayrush.springmvcrest.wallet.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jayrush.springmvcrest.wallet.models.walletTransaction;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WalletTransactionListDTO {
    private boolean hasNextRecord;
    private int totalCount;

    @JsonProperty("transactions")
    private List<walletTransaction> walletTransactions = new ArrayList<>();
}
