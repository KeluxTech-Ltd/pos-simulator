package com.jayrush.springmvcrest.domain.domainDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class TransactionHistoryDTO {
    private String institutionID ;
    private String fromDate;
    private String toDate;
    private int size;
    private int page;
}
