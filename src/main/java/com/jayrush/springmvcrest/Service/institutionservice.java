package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.domainDTO.InstitutionDTO;
import com.jayrush.springmvcrest.domain.domainDTO.InstitutionListDTO;
import com.jayrush.springmvcrest.domain.domainDTO.PagedRequestDTO;
import com.jayrush.springmvcrest.serviceProviders.Models.profiles;

import java.util.List;

public interface institutionservice {
    List<Institution> getAllInstitution();
    Institution getinstitutionbyid(String id);
    Institution registerInstitution(InstitutionDTO institution);
    Institution editInstitution(String institutionID, InstitutionDTO institutionDTO);
    InstitutionListDTO getPagenatedInstitutions(PagedRequestDTO pagedTerminalsDTO);

}
