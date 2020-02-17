package com.jayrush.springmvcrest.serviceProviders.dtos;

import lombok.Data;

/**
 * @author JoshuaO
 */

@Data
public class profilesDTO {
    private long id;
    private String profileName;
    private String profileAddress;
    private int port;
    private String zpk;
    private Double minimumCharge;
    private Double maximumCharge;
    private Double feePercentage;
}
