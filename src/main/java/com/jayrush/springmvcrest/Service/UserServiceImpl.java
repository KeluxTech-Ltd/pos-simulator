package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.Repositories.UserRepository;
import com.jayrush.springmvcrest.domain.tmsUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author JoshuaO
 */

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public List<tmsUser> getInstitutionUsers(String institutionID) {
        List<tmsUser> userList = userRepository.findByInstitution_InstitutionID(institutionID);
        return userList;
    }
}
