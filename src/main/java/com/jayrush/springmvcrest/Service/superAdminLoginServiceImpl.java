package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.Repositories.UserRepository;
import com.jayrush.springmvcrest.Service.email.service.MailService;
import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.domainDTO.DeleteUser;
import com.jayrush.springmvcrest.domain.domainDTO.LoginDTO;
import com.jayrush.springmvcrest.domain.domainDTO.tmsUserDTO;
import com.jayrush.springmvcrest.domain.tmsUser;
import com.jayrush.springmvcrest.exceptions.tmsExceptions;
import com.jayrush.springmvcrest.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = simpleDateFormat.format(new Date());

        tmsUser user = userRepository.findByFirstnameAndEmail(tmsUser.getFirstname(),tmsUser.getEmail());
        if (Objects.isNull(user)){
            tmsUser.setUsername(tmsUser.getEmail());

            Map<String, Object> params = new HashMap<>();
            params.put("institutionName", tmsUser.getInstitution().getInstitutionName());
            params.put("username", tmsUser.getEmail());
            params.put("password", tmsUser.getPassword());
            tmsUser.setDatecreated(date);
            tmsUser.setPassword(passwordEncoder.encode(tmsUser.getPassword()));
            try {
                mailService.sendMail("Medusa User Creation",tmsUser.getEmail(),null,params,"user_template",tmsUser.getInstitution().getInstitutionID());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return userRepository.save(tmsUser);
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
                System.out.println("Incorrect Password");
            }
        }
        else {
            System.out.println("Invalid User");
        }
    }
}
