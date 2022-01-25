package com.jayrush.springmvcrest.rolesPermissions.services;

import com.jayrush.springmvcrest.Repositories.UserRepository;
import com.jayrush.springmvcrest.domain.tmsUser;
import com.jayrush.springmvcrest.jwt.JwtTokenUtil;
import com.jayrush.springmvcrest.rolesPermissions.dtos.rolesDto;
import com.jayrush.springmvcrest.rolesPermissions.models.Roles;
import com.jayrush.springmvcrest.rolesPermissions.repositories.rolesRepository;
import com.jayrush.springmvcrest.slf4j.Logger;
import com.jayrush.springmvcrest.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author JoshuaO
 */

@Service
public class roleServiceImpl implements roleService {
    @Autowired
    rolesRepository rolesRepository;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(roleServiceImpl.class);
    @Override
    public Roles addRoles(rolesDto rolesDto) {
        Roles roles = new Roles();
        roles.setName(rolesDto.getName());
        roles.setDescription(rolesDto.getDescription());
        roles.setPermissions(rolesDto.getPermissions());
        rolesRepository.save(roles);
        return roles;
    }

    @Override
    public List<Roles> getAllRoles() {
        return rolesRepository.findAll();
    }

    @Override
    public Roles editRoles(Roles rolesDto) {
        Roles roles = rolesRepository.findById(rolesDto.getId()).get();
        if (Objects.nonNull(roles)){
            roles.setDescription(rolesDto.getDescription());
            roles.setId(rolesDto.getId());
            roles.setName(rolesDto.getName());
            roles.setPermissions(rolesDto.getPermissions());
            rolesRepository.save(roles);
            return roles;
        }
        return roles;
    }

    @Override
    public String deleteRoles(Roles rolesDto) {
        Roles roles = rolesRepository.findById(rolesDto.getId()).get();
        if (Objects.nonNull(roles)){
            rolesRepository.delete(roles);
            return "Successfully Deleted";
        }
        return "Delete unsuccessful";
    }

    @Override
    public List<Roles> getAllRolesByInstitutionID(String token) {
        String username = jwtTokenUtil.getUsernameFromToken(token);
        if (username == null) {
            logger.info("Invalid token:: User not found");
            return null;
        }
        else {
            tmsUser User = userRepository.findByusername(username);
            if (Objects.nonNull(User)){
                return rolesRepository.findByInstitution_InstitutionID(User.getInstitution().getInstitutionID());
            }else{
                logger.info("User not found");
                return null;
            }
        }

    }
}
