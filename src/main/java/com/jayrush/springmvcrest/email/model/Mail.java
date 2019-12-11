//package com.jayrush.springmvcrest.email.model;
//
//
//import com.jayrush.springmvcrest.entity.AbstractEntity;
//import lombok.Data;
//import lombok.ToString;
//
//import javax.persistence.Column;
//import javax.persistence.ElementCollection;
//import javax.persistence.Entity;
//import javax.persistence.Lob;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//@ToString
//@Entity
//@Data
//public class Mail extends AbstractEntity {
//
//    private String mailHeader ;
//
//    @Column(nullable = false)
//    private String mailTo ;
//
//    @Lob
//    private String mailContent;
//
//    @ElementCollection
//    private List<String> copy ;
//
//    @ElementCollection
//    private Map<String, String> attachements;
//
//    private boolean sent = false;
//
//    private String tmsAdminName;
//
//    @Lob
//    private String failureReason ;
//
//    private Date createdOn = new Date();
//
//    private Date lastSent ;
//
//
//
//}
