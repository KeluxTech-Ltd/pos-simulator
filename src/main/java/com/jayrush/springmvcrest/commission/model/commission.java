package com.jayrush.springmvcrest.commission.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author JoshuaO
 */

@Entity
@Data
public class commission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String institutionID;
    private Long tranID;
    private Double amount;
    private Double iswFee;
    private Double tmsFee;
    private Double institutionAmount;
    private String Date;

}
