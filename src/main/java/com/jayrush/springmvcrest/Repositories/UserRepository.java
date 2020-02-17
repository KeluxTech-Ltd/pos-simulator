package com.jayrush.springmvcrest.Repositories;

import com.jayrush.springmvcrest.domain.tmsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<tmsUser,Long > {
    tmsUser findByusername(String username);
    tmsUser findByFirstnameAndEmail(String firstname,String email);
    List<tmsUser>findByInstitution_InstitutionID(String institutionID);
}
