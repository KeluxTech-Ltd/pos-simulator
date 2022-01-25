package com.jayrush.springmvcrest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jayrush.springmvcrest.rolesPermissions.models.Roles;
import lombok.Data;

import javax.persistence.*;

@Data
public class TmsUserDTO {
    private String firstname;

    private String lastname;

    private String email;

    private String datecreated;

    private String username;

    private String password;

    @OneToOne(cascade = {CascadeType.ALL})
    private Institution institution;
    @OneToOne
    private Roles role;
    @JsonIgnore
    private boolean changePassword;

    private String token;
}
