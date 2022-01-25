package com.jayrush.springmvcrest.rolesPermissions.repositories;

import com.jayrush.springmvcrest.rolesPermissions.models.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author JoshuaO
 */
@Repository
public interface permissionRepository extends JpaRepository<Permissions,Long> {
    Permissions findByName(String name);
}
