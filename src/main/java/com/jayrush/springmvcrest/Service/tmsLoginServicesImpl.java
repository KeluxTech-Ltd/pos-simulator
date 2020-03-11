package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.Repositories.UserRepository;
import com.jayrush.springmvcrest.Service.email.service.MailService;
import com.jayrush.springmvcrest.domain.*;
import com.jayrush.springmvcrest.domain.domainDTO.InstitutionDTO;
import com.jayrush.springmvcrest.domain.domainDTO.LoginDTO;
import com.jayrush.springmvcrest.domain.domainDTO.tmsUserDTO;
import com.jayrush.springmvcrest.exceptions.tmsExceptions;
import com.jayrush.springmvcrest.jwt.JwtTokenUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class tmsLoginServicesImpl implements tmsLoginServices {
    private Logger logger = LoggerFactory.getLogger(this.getClass()) ;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MailService mailService;

    @Autowired
    InstitutionRepository institutionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Response login(LoginDTO loginDto) {
        logger.info("Begining Login Service");
        Response response = new Response();

        tmsUser User = userRepository.findByusername(loginDto.getUsername());

        if(User != null){
            if(!passwordEncoder.matches(loginDto.getPassword(),User.getPassword())){
                throw new tmsExceptions("Invalid User Password");
            }
            System.out.println("attached token using :: "+User.getUsername());
            String token = jwtTokenUtil.generateToken(User.getUsername());
            User.setToken(token);
            User.setRole(User.getRole());
            User.setInstitution(User.getInstitution());
            User.setEmail(User.getEmail());
            User.setFirstname(User.getFirstname());
            User.setLastname(User.getLastname());
            response.setRespCode("00");
            response.setRespDescription("Success");
            response.setRespBody(User);
            return response;
        }else{
            throw new tmsExceptions("Invalid Institution Name");
        }
    }

    @Override
    public Response CreateInstitutionUser(tmsUser User) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        Map<String, Object> params = new HashMap<>();
        params.put("institutionName", User.getInstitution().getInstitutionName());
        params.put("institutionEmail", User.getInstitution().getInstitutionEmail());
        params.put("institutionID", User.getInstitution().getInstitutionID());
        params.put("institutionPassword", User.getPassword());

        User.setPassword(passwordEncoder.encode(User.getPassword()));

        try {
            mailService.sendMail("Institution User Creation",User.getEmail(),null,params,"user_template",User.getInstitution().getInstitutionID());
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        User.setDatecreated(date);
        tmsUser user = userRepository.save(User);
        Response response = new Response();
        response.setRespCode("00");
        response.setRespDescription("Success");
        response.setRespBody(user);
        return response;
    }

}
