package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.domainDTO.LoginDTO;
import com.jayrush.springmvcrest.domain.domainDTO.LoginDTOresponse;
import com.jayrush.springmvcrest.domain.tmsUser;

import java.util.List;

public interface superAdminLoginService {
    Institution superAdminCreateInstitution(Institution institution);
    Response superAdminLogin(LoginDTO loginDTO);
    List<Institution>superAdminViewInstitutions();
    List<tmsUser>superAdminViewUsers();
    tmsUser superAdminCreateUsers(tmsUser tmsUser);
    tmsUser superAdminGetUser(Long id);
    Institution superAdminGetInstitution(Long id);
    void superAdminDeleteUsers(tmsUser tmsUser);
}
