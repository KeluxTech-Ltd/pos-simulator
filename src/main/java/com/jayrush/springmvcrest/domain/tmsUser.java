package com.jayrush.springmvcrest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
public class tmsUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;

    private String lastname;

    private String email;

    private String datecreated;

    @JsonIgnore
    private String username;

    private String password;


    @ManyToOne(cascade = {CascadeType.ALL})
    private Institution institution;

    private roleType role;
    @JsonIgnore
    private boolean changePassword;
}
