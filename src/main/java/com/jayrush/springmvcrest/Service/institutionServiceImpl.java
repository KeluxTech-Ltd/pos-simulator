package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.utility.AppUtility;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class institutionServiceImpl implements institutionService {
    @Autowired
    InstitutionRepository institutionRepository;


    @Override
    public List<Institution> getAllInstitution() {
        return institutionRepository.findAll();
    }

    @Override
    public Institution getInstitutionByID(Long id) {
        return institutionRepository.findById(id).get();
    }

    @Override
    public Institution RegisterInstitution(Institution institution) {
        Date date = new Date();
        institution.setDateCreated(date.toString());
        String institutionID = StringUtils.substring(institution.getInstitutionName(), 0, 4).toUpperCase()+AppUtility.randomNumber(6);
        institution.setInstitutionID(institutionID);

        return institutionRepository.save(institution);
    }

    @Override
    public Institution EditInstitution(Institution institution) {
        return institutionRepository.save(institution);
    }
}
