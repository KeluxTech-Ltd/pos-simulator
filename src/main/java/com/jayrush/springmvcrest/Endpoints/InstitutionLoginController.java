package com.jayrush.springmvcrest.Endpoints;

import com.google.gson.Gson;
import com.jayrush.springmvcrest.Service.tmsInstitutionService;
import com.jayrush.springmvcrest.Service.tmsLoginServices;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.domainDTO.LoginDTO;
import com.jayrush.springmvcrest.domain.tmsUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1")
@RestController()
public class InstitutionLoginController {
    @Autowired
    tmsInstitutionService tmsInstitutionService;
    @Autowired
    tmsLoginServices tmsLoginService;

    @Autowired
    PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody  LoginDTO request)
    {
        try
        {
            Response response = tmsLoginService.login(request);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/CreateInstitutionUser")
    public ResponseEntity<?> Create (@RequestBody tmsUser request)
    {
        try
        {
            Response response = tmsLoginService.CreateInstitutionUser(request);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @RequestMapping(
            value = "/**",
            method = RequestMethod.OPTIONS
    )
    public ResponseEntity handle() {
        return new ResponseEntity(HttpStatus.OK);
    }

}
