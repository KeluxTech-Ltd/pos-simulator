package com.jayrush.springmvcrest.Repositories;

import com.jayrush.springmvcrest.domain.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    Institution findByInstitutionID(String institutionID);
    Institution findByinstitutionName(String institutionName);
}
