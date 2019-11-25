package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.domainDTO.InstitutionListDTO;
import com.jayrush.springmvcrest.domain.domainDTO.PagedRequestDTO;
import com.jayrush.springmvcrest.utility.AppUtility;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class institutionServiceImpl implements institutionservice {
    @Autowired
    InstitutionRepository institutionRepository;


    @Override
    public List<Institution> getAllInstitution() {
        return institutionRepository.findAll();
    }

    @Override
    public Institution getinstitutionbyid(Long id) {
        return institutionRepository.findById(id).get();
    }

    @Override
    public Institution registerInstitution(Institution institution) {
        Date date = new Date();
        institution.setDateCreated(date.toString());
        String institutionID = StringUtils.substring(institution.getInstitutionName(), 0, 4).toUpperCase()+AppUtility.randomNumber(6);
        institution.setInstitutionID(institutionID);

        return institutionRepository.save(institution);
    }

    @Override
    public Institution editInstitution(Institution institution) {
        return institutionRepository.save(institution);
    }

    @Override
    public InstitutionListDTO getPagenatedInstitutions(PagedRequestDTO pagedRequestDTO) {
        InstitutionListDTO institutionListDTO = new InstitutionListDTO();
        List<Institution> InstitutionResp;
        Page<Institution> pagedInstitutions;
        Pageable paged;

        if (pagedRequestDTO.getSize()>0 && pagedRequestDTO.getPage()>=0){
            paged = PageRequest.of(pagedRequestDTO.getPage(),pagedRequestDTO.getSize());
        }
        else {
            paged = PageRequest.of(0,1000000);
        }

        pagedInstitutions = institutionRepository.findAll(paged);
        InstitutionResp = pagedInstitutions.getContent();
        if (pagedInstitutions!=null && pagedInstitutions.getContent().size()>0){
            institutionListDTO.setHasNextRecord(pagedInstitutions.hasNext());
            institutionListDTO.setTotalCount((int) pagedInstitutions.getTotalElements());
        }
        institutionListDTO.setTerminals(InstitutionResp);

        return institutionListDTO;
    }

}
