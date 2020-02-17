package com.jayrush.springmvcrest.commission.model.dtos;

import lombok.Data;

@Data
public class CommissionsHistoryDTO {
    private String institutionID;
    private String fromDate;
    private String toDate;
    private int size;
    private int page;
}
