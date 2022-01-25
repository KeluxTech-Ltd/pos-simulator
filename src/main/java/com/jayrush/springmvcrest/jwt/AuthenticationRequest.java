package com.jayrush.springmvcrest.jwt;

import lombok.Data;

@Data
public class AuthenticationRequest {

    private String username;
    private String password;


}
