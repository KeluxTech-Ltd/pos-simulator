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
public class globalSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Generates the ID for us
    private Long id;
    private boolean Settings = false;
}
