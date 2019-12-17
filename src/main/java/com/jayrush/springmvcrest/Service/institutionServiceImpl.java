package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.Repositories.UserRepository;
import com.jayrush.springmvcrest.Service.email.service.MailService;
import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.domainDTO.InstitutionDTO;
import com.jayrush.springmvcrest.domain.domainDTO.InstitutionListDTO;
import com.jayrush.springmvcrest.domain.domainDTO.PagedRequestDTO;
import com.jayrush.springmvcrest.domain.roleType;
import com.jayrush.springmvcrest.domain.tmsUser;
import com.jayrush.springmvcrest.serviceProviders.Models.profiles;
import com.jayrush.springmvcrest.serviceProviders.Models.serviceProviders;
import com.jayrush.springmvcrest.serviceProviders.repository.serviceProviderRepo;
import com.jayrush.springmvcrest.utility.AppUtility;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class institutionServiceImpl implements institutionservice {
    @Autowired
    InstitutionRepository institutionRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MailService mailService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    serviceProviderRepo serviceProviderRepo;


    @Override
    public List<Institution> getAllInstitution() {
        return institutionRepository.findAll();
    }

    @Override
    public Institution getinstitutionbyid(Long id) {
        return institutionRepository.findById(id).get();
    }

    @Override
    public Institution registerInstitution(InstitutionDTO institution) {
        Institution institution1 = new Institution();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = simpleDateFormat.format(new Date());

        String institutionID = StringUtils.substring(institution.getInstitutionName(), 0, 4).toUpperCase()+AppUtility.randomNumber(6);

        serviceProviders providers = serviceProviderRepo.findByProviderName(institution.getServiceProviderName());
        institution1.setDateCreated(date);
        institution1.setInstitutionID(institutionID);
        institution1.setInstitutionName(institution.getInstitutionName().toUpperCase());
        institution1.setInstitutionEmail(institution.getInstitutionEmail());
        institution1.setInstitutionPhone(institution.getInstitutionPhone());
        institution1.setSettlementAccount(institution.getSettlementAccount());
        institution1.setCreatedBy(institution.getCreatedBy());
        institution1.setBank(institution.getBank());
        institution1.setServiceProviders(providers);

        Institution institution2 = institutionRepository.findByinstitutionName(institution.getInstitutionName());

        if (Objects.nonNull(institution2)){
            institution1.setSaved(false);
            institution1.setSavedDescription("Institution already exists");
            return institution1;
        }
        else if (Objects.isNull(providers)){
            institution1.setSaved(false);
            institution1.setSavedDescription("Service providers not found");
            return institution1;
        }
        else {
            institutionUserCreation(institution1);
            institution1.setSavedDescription(null);
            institution1.setSaved(true);
            return institutionRepository.save(institution1);
        }


    }
    //method for creating an institution as a user on the tms
    private void institutionUserCreation(@RequestBody Institution institution) {
        tmsUser User = new tmsUser();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        User.setEmail(institution.getInstitutionEmail());
        User.setDatecreated(date);
        User.setFirstname(institution.getInstitutionName());
        User.setInstitution(institution);
        User.setRole(roleType.SuperAdmin);
        User.setUsername(institution.getInstitutionEmail());
        String pass = AppUtility.randomString(10);
        String body = "Username: "+ institution.getInstitutionEmail()+ " password :"+pass;
        String password = passwordEncoder.encode(pass);
        User.setPassword(password);
        User.setChangePassword(true);
        Map<String, Object> params = new HashMap<>();
        params.put("institutionName", institution.getInstitutionName());
        params.put("institutionEmail", institution.getInstitutionEmail());
        params.put("institutionID", institution.getInstitutionID());
        params.put("institutionPassword", pass);
        try {
            mailService.sendMail("Medusa Institution Creation",institution.getInstitutionEmail(),null,params,"institution_creation",institution.getInstitutionID());
            userRepository.save(User);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
