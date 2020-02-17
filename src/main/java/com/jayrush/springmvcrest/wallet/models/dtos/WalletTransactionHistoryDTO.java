package com.jayrush.springmvcrest.wallet.models.dtos;

import lombok.Data;

@Data
public class WalletTransactionHistoryDTO {
    private String walletNumber ;
    private String fromDate;
    private String toDate;
    private int size;
    private int page;
}
