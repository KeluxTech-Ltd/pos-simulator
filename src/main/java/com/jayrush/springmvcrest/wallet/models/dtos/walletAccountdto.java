package com.jayrush.springmvcrest.wallet.models.dtos;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author JoshuaO
 */
@Data
public class walletAccountdto {
    private String institutionID ;
    private String purpose ;
    private Double minimumCharge;
    private Double maximumCharge;
    private Double feePercentage;

}
