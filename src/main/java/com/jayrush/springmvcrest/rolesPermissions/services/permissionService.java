package com.jayrush.springmvcrest.rolesPermissions.services;

import com.jayrush.springmvcrest.rolesPermissions.dtos.permissionDto;
import com.jayrush.springmvcrest.rolesPermissions.models.Permissions;

import java.util.List;

/**
 * @author JoshuaO
 */
public interface permissionService {
    List<Permissions> getAllPermissions();
}
