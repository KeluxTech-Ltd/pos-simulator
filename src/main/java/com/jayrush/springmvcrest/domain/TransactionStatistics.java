package com.jayrush.springmvcrest.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author JoshuaO
 */
@Data
public class TransactionStatistics {
    private int totalTransactions;
    private BigDecimal totalSuccessfulAmount;
    private int success;
    private int failed;
}
