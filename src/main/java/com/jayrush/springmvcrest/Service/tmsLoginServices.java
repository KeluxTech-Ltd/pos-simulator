package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.domainDTO.LoginDTO;
import com.jayrush.springmvcrest.domain.tmsUser;
import org.springframework.scheduling.annotation.Scheduled;

public interface tmsLoginServices {
    Response login(LoginDTO loginDto);
    Response CreateInstitutionUser(tmsUser loginDto);
    //Response CreateUsers(LoginDTO loginDto);

}
