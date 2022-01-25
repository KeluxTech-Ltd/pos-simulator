package com.jayrush.springmvcrest.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author JoshuaO
 */
@Data
public class DashboardUtils {
    private Object topFiveActiveInstitutions;
    private Object totalInstitutions;
    private Object totalTerminals;
    private Object activeInactiveTerminals;
    private Object totalTransactions;
    private Object success;
    private Object failed;
    private Object totalSuccessfulAmount;
    private Object dashboardTransactions;
}
