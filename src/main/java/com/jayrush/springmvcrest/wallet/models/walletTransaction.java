package com.jayrush.springmvcrest.wallet.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

/**
 * @author JoshuaO
 */

@Entity
@Data
public class walletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @JsonIgnore
    @ManyToOne
    private walletAccount wallet;

    @Enumerated(EnumType.STRING)
    private TranType tranType;

    private Double amount ;

    private String channel ;

    private String remark ;

    private String tranID ;


    private Double balanceBefore ;

    private Double balanceAfter ;

    private Double balanceBefore_reconcilled ;

    private Double balanceAfter_reconcilled ;

    private String walletNumber ;

    private boolean reversed = false ;

    private Long reversalTranId ;

    private String tranDate;

}
