package com.jayrush.springmvcrest.domain.domainDTO;

import lombok.Data;

@Data
public class DeleteUser {
    private Long userToDeleteid;

    private String username;

    private String password;

}
