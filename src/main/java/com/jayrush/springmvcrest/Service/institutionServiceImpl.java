package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.Repositories.UserRepository;
import com.jayrush.springmvcrest.Service.email.service.MailService;
import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.domainDTO.InstitutionDTO;
import com.jayrush.springmvcrest.domain.domainDTO.InstitutionListDTO;
import com.jayrush.springmvcrest.domain.domainDTO.PagedRequestDTO;
import com.jayrush.springmvcrest.domain.tmsUser;
import com.jayrush.springmvcrest.jwt.JwtTokenUtil;
import com.jayrush.springmvcrest.rolesPermissions.models.Permissions;
import com.jayrush.springmvcrest.rolesPermissions.models.Roles;
import com.jayrush.springmvcrest.rolesPermissions.repositories.permissionRepository;
import com.jayrush.springmvcrest.rolesPermissions.repositories.rolesRepository;
import com.jayrush.springmvcrest.serviceProviders.Models.serviceProviders;
import com.jayrush.springmvcrest.serviceProviders.repository.serviceProviderRepo;
import com.jayrush.springmvcrest.utility.AppUtility;
import com.jayrush.springmvcrest.wallet.models.dtos.walletAccountdto;
import com.jayrush.springmvcrest.wallet.models.walletAccount;
import com.jayrush.springmvcrest.wallet.repository.walletAccountRepository;
import com.jayrush.springmvcrest.wallet.service.walletServices;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    @Autowired
    rolesRepository rolesRepository;
    @Autowired
    walletServices walletServices;
    @Autowired
    walletAccountRepository walletAccountRepository;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    permissionRepository permissionRepository;
    @Autowired
    ModelMapper modelMapper;

    private Logger logger = LoggerFactory.getLogger(institutionServiceImpl.class);



    @Override
    public List<Institution> getAllInstitution() {
        return institutionRepository.findAll();
    }

    @Override
    public Institution getinstitutionbyid(String id) {
        return institutionRepository.findByInstitutionID(id);

    }

    @Override
    public Institution registerInstitution(InstitutionDTO institution) {
        String tmsUserUsername = jwtTokenUtil.getUsernameFromToken(institution.getToken());
        Permissions permissions = permissionRepository.findByName("CREATE_INSTITUTION");


        tmsUser User1 = userRepository.findByusername(tmsUserUsername);

        if ((Objects.nonNull(User1))&&User1.getRole().getPermissions().contains(permissions)){
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
            institution1.setInstitutionAppKey(institution.getInstitutionAppKey());
            institution1.setInstitutionURL(institution.getInstitutionURL());
            institution1.setInstitutionIntegrationVersion(institution.getInstitutionIntegrationVersion());

            Institution institution2 = institutionRepository.findByinstitutionNameAndInstitutionEmail(institution.getInstitutionName(),institution.getInstitutionEmail());

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

                walletAccount walletAccount = walletAccountRepository.findByWalletNumber(institution1.getInstitutionID());
                if (Objects.isNull(walletAccount)){
                    walletAccountdto walletAccountdto = new walletAccountdto();
                    Double fee = institution.getFeePercentage()/100;
                    walletAccountdto.setFeePercentage(fee);
                    walletAccountdto.setInstitutionID(institutionID);
                    walletAccountdto.setMaximumCharge(institution.getMaximumCharge());
                    walletAccountdto.setMinimumCharge(institution.getMinimumCharge());
                    walletAccountdto.setPurpose(institution.getInstitutionName()+" Wallet Account");
                    walletServices.createWalletAccount(walletAccountdto);
                }
                else {
                    logger.info("Wallet Account already Exists for {}",institution1.getInstitutionName());
                }
                institution1.setGlobalSetting(institution.getGlobalSettings());
                return institutionRepository.save(institution1);
            }
        }
        else {
            logger.info("Requires CREATE_INSTITUTION permission");
            return null;
        }




    }

    @Override
    public Institution editInstitution(String institutionID, InstitutionDTO institutionDTO) {
        Institution institution = institutionRepository.findByInstitutionID(institutionID);
        if (Objects.nonNull(institution)){
            String tmsUserUsername = jwtTokenUtil.getUsernameFromToken(institutionDTO.getToken());
            Permissions permissions = permissionRepository.findByName("GLOBAL_SETTINGS");
            tmsUser User1 = userRepository.findByusername(tmsUserUsername);
            if ((Objects.nonNull(User1))&&User1.getRole().getPermissions().contains(permissions)){
                serviceProviders providers = serviceProviderRepo.findByProviderName(institutionDTO.getServiceProviderName());
                if (Objects.isNull(providers)){
                    logger.info("No service provider for {}",institutionDTO.getServiceProviderName());
                }
                institution.setInstitutionName(institutionDTO.getInstitutionName());
                institution.setInstitutionEmail(institutionDTO.getInstitutionEmail());
                institution.setInstitutionPhone(institutionDTO.getInstitutionPhone());
                institution.setSettlementAccount(institutionDTO.getSettlementAccount());
                institution.setServiceProviders(providers);
                institution.setBank(institutionDTO.getBank());
                institution.setInstitutionURL(institutionDTO.getInstitutionURL());
                institution.setInstitutionAppKey(institutionDTO.getInstitutionAppKey());
                institution.setInstitutionIntegrationVersion(institutionDTO.getInstitutionIntegrationVersion());
                institution.setGlobalSetting(institutionDTO.getGlobalSettings());
                institutionRepository.save(institution);
                return institution;
            }else {
                logger.info("Permission not available for User");
                return null;
            }
        }
        else {
            logger.info("Institution not found for {}", institutionID);
            return null;
        }
    }

    //method for creating an institution as a user on the tms
    private void institutionUserCreation(@RequestBody Institution institution) {
        Roles role = rolesRepository.findByName("ADMIN");
        tmsUser User = new tmsUser();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        User.setEmail(institution.getInstitutionEmail());
        User.setDatecreated(date);
        User.setFirstname(institution.getInstitutionName());
        User.setInstitution(institution);
        User.setRole(role);
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
            mailService.sendMail("Medusa Institution Creation",institution.getInstitutionEmail(),null,params,"institution_template",institution.getInstitutionID());
            userRepository.save(User);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
