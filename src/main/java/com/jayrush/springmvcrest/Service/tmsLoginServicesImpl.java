package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.Repositories.UserRepository;
import com.jayrush.springmvcrest.domain.*;
import com.jayrush.springmvcrest.domain.domainDTO.InstitutionDTO;
import com.jayrush.springmvcrest.domain.domainDTO.LoginDTO;
import com.jayrush.springmvcrest.domain.domainDTO.tmsUserDTO;
import com.jayrush.springmvcrest.email.MailService;
import com.jayrush.springmvcrest.exceptions.tmsExceptions;
import com.jayrush.springmvcrest.jwt.JwtTokenUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

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
            tmsUserDTO UserDTO = new tmsUserDTO();
            System.out.println("attached token using :: "+User.getUsername());
            String token = jwtTokenUtil.generateToken(User.getUsername());
            UserDTO.setAuthToken(token);
            UserDTO.setRole(User.getRole());
            UserDTO.setInstitution(User.getInstitution());
            UserDTO.setEmail(User.getEmail());
            UserDTO.setFirstname(User.getFirstname());
            UserDTO.setLastname(User.getLastname());
            response.setRespCode("00");
            response.setRespDescription("Success");
            response.setRespBody(UserDTO);
            return response;
        }else{
            throw new tmsExceptions("Invalid Institution Name");
        }
    }

    @Override
    public Response CreateInstitutionUser(tmsUser User) {
        Date date = new Date();
        String body = "TMS User Details"+"\n\n\n\n" +
                "username: "+User.getUsername()+"\n\n\n"
                +"Password: "+User.getPassword();
        User.setPassword(passwordEncoder.encode(User.getPassword()));
        mailService.SendMail(User.getEmail(),body);
        User.setDatecreated(date.toString());
        tmsUser user = userRepository.save(User);
        Response response = new Response();
        response.setRespCode("00");
        response.setRespDescription("Success");
        response.setRespBody(user);
        return response;
    }

}
