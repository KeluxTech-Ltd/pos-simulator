package com.jayrush.springmvcrest.wallet.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author JoshuaO
 */
@Entity
@Data
public class walletAccount {
    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    Long id;

    @Column(unique = true)
    String walletNumber ;

    protected String delFlag = "N";

    protected Date deletedOn;

    protected final Date createdOn = new Date();

    Double availableBalance = 0.0 ;

    Double ledgerBalance = 0.0  ;

    Date lastTranDate ;

    String purpose ;

    String isGeneralLedger = "NO";
    private Double minimumCharge;
    private Double maximumCharge;
    private Double feePercentage;
//    private boolean flatfee = false;
//    private Double flatfeeAmount;

}
