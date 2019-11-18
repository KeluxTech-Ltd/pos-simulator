package com.jayrush.springmvcrest.domain;

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

    private String username;

    private String password;

    @OneToOne
    private Institution institution;

    private roleType role;
}
