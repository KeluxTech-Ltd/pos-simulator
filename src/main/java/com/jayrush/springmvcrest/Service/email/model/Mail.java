package com.jayrush.springmvcrest.Service.email.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ToString
@Entity
@Data
public class Mail  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Generates the ID for us

    private Long id;

    private String mailHeader ;

    @Column(nullable = false)
    private String mailTo ;

    @Lob
    private String mailContent;

    @ElementCollection
    private List<String> copy ;

    @ElementCollection
    private Map<String, String> attachements;

    private boolean sent = false;

    private String institutionID;

    @Lob
    private String failureReason ;

    private Date createdOn = new Date();

    private Date lastSent ;



}
