package com.jayrush.springmvcrest.rolesPermissions.services;

import com.jayrush.springmvcrest.rolesPermissions.dtos.rolesDto;
import com.jayrush.springmvcrest.rolesPermissions.models.Roles;

import java.util.List;

/**
 * @author JoshuaO
 */
public interface roleService {
    Roles addRoles(rolesDto rolesDto);
    List<Roles> getAllRoles();
    Roles editRoles(Roles rolesDto);
    String deleteRoles(Roles rolesDto);

    List<Roles> getAllRolesByInstitutionID(String token);
}
