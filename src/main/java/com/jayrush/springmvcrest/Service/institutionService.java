package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.domain.Institution;

import java.util.List;

public interface institutionService {
    List<Institution> getAllInstitution();
    Institution getInstitutionByID(Long id);
    Institution RegisterInstitution(Institution institution);
    Institution EditInstitution(Institution institution);
}
