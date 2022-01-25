package com.jayrush.springmvcrest.domain;

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
public class terminalKeyManagement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String terminalID;
    private String masterKey;
    private String sessionKey;
    private String pinKey;
    private String parameterDownloaded;
    private String lastExchangeDateTime;
}
