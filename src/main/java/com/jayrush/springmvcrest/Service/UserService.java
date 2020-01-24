package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.domain.tmsUser;

import java.util.List;

/**
 * @author JoshuaO
 */
public interface UserService {
    List<tmsUser>getInstitutionUsers(String institutionName);
}
