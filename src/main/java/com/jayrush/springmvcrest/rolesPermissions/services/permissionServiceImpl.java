package com.jayrush.springmvcrest.rolesPermissions.services;

import com.jayrush.springmvcrest.rolesPermissions.dtos.permissionDto;
import com.jayrush.springmvcrest.rolesPermissions.models.Permissions;
import com.jayrush.springmvcrest.rolesPermissions.repositories.permissionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author JoshuaO
 */
@Service
public class permissionServiceImpl implements permissionService {
    @Autowired
    permissionRepository permissionRepository;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public List<Permissions> getAllPermissions() {
        return permissionRepository.findAll();
    }



}
