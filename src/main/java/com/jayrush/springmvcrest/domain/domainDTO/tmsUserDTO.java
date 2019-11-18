package com.jayrush.springmvcrest.domain.domainDTO;

import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.roleType;
import lombok.Data;

@Data
public class tmsUserDTO {
    private String firstname;

    private String lastname;

    private String email;

    private Institution institution;

    private roleType role;

    private String authToken;
}
