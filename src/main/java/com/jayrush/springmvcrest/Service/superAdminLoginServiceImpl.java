package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.Repositories.UserRepository;
import com.jayrush.springmvcrest.Service.email.service.MailService;
import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.domainDTO.DeleteUser;
import com.jayrush.springmvcrest.domain.domainDTO.LoginDTO;
import com.jayrush.springmvcrest.domain.tmsUser;
import com.jayrush.springmvcrest.exceptions.tmsExceptions;
import com.jayrush.springmvcrest.jwt.JwtTokenUtil;
import com.jayrush.springmvcrest.rolesPermissions.models.Permissions;
import com.jayrush.springmvcrest.rolesPermissions.repositories.permissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class superAdminLoginServiceImpl implements superAdminLoginService {
    @Autowired
    InstitutionRepository institutionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    MailService mailService;
    @Autowired
    permissionRepository permissionRepository;

    private Logger logger = LoggerFactory.getLogger(superAdminLoginServiceImpl.class);


    @Override
    public Response superAdminLogin(LoginDTO loginDTO) {
        Response response = new Response();
        tmsUser User = userRepository.findByusername(loginDTO.getUsername());

        if(User != null){
            if(!passwordEncoder.matches(loginDTO.getPassword(),User.getPassword())){
                throw new tmsExceptions("Invalid User Password");
            }

            String token = jwtTokenUtil.generateToken(User.getUsername());
            User.setToken(token);
            User.setInstitution(null);
            response.setRespCode("00");
            response.setRespDescription("Success");
            response.setRespBody(User);
            return response;
        }else{
            throw new tmsExceptions("Invalid User Name");
        }
    }

    @Override
    public Response getToken() {
        Response response = new Response();
        String token = jwtTokenUtil.generateToken("SuperAdmin");
        response.setRespBody(token);
        return response;
    }


    @Override
    public Institution superAdminCreateInstitution(Institution institution) {
        return institutionRepository.save(institution);
    }


    @Override
    public List<Institution> superAdminViewInstitutions() {
        return institutionRepository.findAll();
    }

    @Override
    public List<tmsUser> superAdminViewUsers() {
        return userRepository.findAll();
    }

    @Override
    public tmsUser superAdminCreateUsers(tmsUser tmsUser) {
        Date date= new Date();
        String tmsUserUsername = jwtTokenUtil.getUsernameFromToken(tmsUser.getToken());
        Permissions permissions = permissionRepository.findByName("CREATE_USER");

        tmsUser User = userRepository.findByusername(tmsUserUsername);
        Institution institution = institutionRepository.findById(tmsUser.getInstitution().getId()).get();
        if ((Objects.nonNull(User)) && User.getRole().getPermissions().contains(permissions)){
            tmsUser user = userRepository.findByFirstnameAndEmail(tmsUser.getFirstname(),tmsUser.getEmail());
            if (Objects.isNull(user)){
                tmsUser.setUsername(tmsUser.getEmail());
                tmsUser.setInstitution(institution);
                Map<String, Object> params = new HashMap<>();
                params.put("institutionName", tmsUser.getInstitution().getInstitutionName());
                params.put("username", tmsUser.getEmail());
                params.put("password", tmsUser.getPassword());
                tmsUser.setDatecreated(date.toString());
                tmsUser.setPassword(passwordEncoder.encode(tmsUser.getPassword()));
                try {
                    mailService.sendMail("Medusa User Creation",tmsUser.getEmail(),null,params,"user_template",tmsUser.getInstitution().getInstitutionID());
                } catch (Exception e) {
                    logger.info(e.getMessage());
                }
                return userRepository.save(tmsUser);
            }
        }

        return tmsUser;
    }

    @Override
    public tmsUser superAdminGetUser(Long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public Institution superAdminGetInstitution(Long id) {
        return institutionRepository.findById(id).get();
    }

    @Override
    public void superAdminDeleteUsers(DeleteUser deleteUser) {
        tmsUser tmsUser = userRepository.findByusername(deleteUser.getUsername());
        if (!tmsUser.equals(null)){
            if (passwordEncoder.matches(deleteUser.getPassword(),tmsUser.getPassword())){
                tmsUser tmsUser1 = userRepository.findById(deleteUser.getIdToDelete()).get();
                userRepository.delete(tmsUser1);
            }
            else {
                logger.info("Incorrect Password");
            }
        }
        else {
            logger.info("Invalid User");
        }
    }
}
