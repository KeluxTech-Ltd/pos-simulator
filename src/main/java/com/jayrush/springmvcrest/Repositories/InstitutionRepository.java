package com.jayrush.springmvcrest.Repositories;

import com.jayrush.springmvcrest.domain.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    Institution findByInstitutionID(String institutionID);
    Institution findByinstitutionNameAndInstitutionEmail(String institutionName,String Email);
    Institution findByinstitutionName(String institutionName);
}
