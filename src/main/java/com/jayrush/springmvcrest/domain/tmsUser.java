package com.jayrush.springmvcrest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jayrush.springmvcrest.rolesPermissions.models.Roles;
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

//    @JsonIgnore
    private String username;
//    @JsonIgnore
    private String password;


    @ManyToOne(cascade = {CascadeType.ALL})
    private Institution institution;
    @OneToOne
    private Roles role;
    @JsonIgnore
    private boolean changePassword;

    private String token;
}
