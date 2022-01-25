package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.domainDTO.InstitutionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class tmsInstitutionServiceImpl implements tmsInstitutionService {
    @Autowired
    InstitutionRepository institutionRepository;

    @Override
    public Institution getInstitutionbyID(String institutionName) {
//        Institution institution = institutionRepository.findByusername(institutionName);
        return null;
    }
}
