package com.jayrush.springmvcrest.rolesPermissions.models;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "permissions")
public class Permissions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;



    @Override
    public String toString() {
        return "Permissions{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }


}