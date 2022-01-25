package com.jayrush.springmvcrest.serviceProviders.Models;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.CascadeType.*;

/**
 * @author JoshuaO
 */
@Entity
@Data
public class profiles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String profileName;
    private String profileIP;
    private int port;
    private String zpk;
    @ManyToOne(cascade={PERSIST, MERGE, REMOVE, REFRESH, DETACH})
    private serviceProviders serviceProviders;

}
