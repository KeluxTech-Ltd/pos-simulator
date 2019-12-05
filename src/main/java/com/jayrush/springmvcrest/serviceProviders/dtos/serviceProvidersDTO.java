package com.jayrush.springmvcrest.serviceProviders.dtos;

import lombok.Data;

import java.util.List;

/**
 * @author JoshuaO
 */

@Data
public class serviceProvidersDTO {
    private long id;
    private String providerName;
    private List<profilesDTO> Profile;
}
