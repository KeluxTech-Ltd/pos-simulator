package com.jayrush.springmvcrest.rolesPermissions.repositories;

import com.jayrush.springmvcrest.rolesPermissions.models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author JoshuaO
 */
@Repository
public interface rolesRepository extends JpaRepository<Roles,Long> {
    Roles findByName(String roleName);
    List<Roles>findByInstitution_InstitutionID(String institutionID);

}
