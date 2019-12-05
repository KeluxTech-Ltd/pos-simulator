package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.domainDTO.InstitutionDTO;
import com.jayrush.springmvcrest.domain.domainDTO.InstitutionListDTO;
import com.jayrush.springmvcrest.domain.domainDTO.PagedRequestDTO;

import java.util.List;

public interface institutionservice {
    List<Institution> getAllInstitution();
    Institution getinstitutionbyid(Long id);
    Institution registerInstitution(InstitutionDTO institution);
    Institution editInstitution(Institution institution);
    InstitutionListDTO getPagenatedInstitutions(PagedRequestDTO pagedTerminalsDTO);
}
