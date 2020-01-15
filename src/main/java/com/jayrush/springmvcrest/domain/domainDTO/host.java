package com.jayrush.springmvcrest.domain.domainDTO;

import lombok.Data;

/**
 * @author JoshuaO
 */
@Data
public class host {
    private String hostName;
    private String hostIp;
    private int hostPort;
}
