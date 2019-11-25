package com.jayrush.springmvcrest.Endpoints;

import com.jayrush.springmvcrest.Service.superAdminLoginService;
import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.domainDTO.DeleteUser;
import com.jayrush.springmvcrest.domain.domainDTO.LoginDTO;
import com.jayrush.springmvcrest.domain.tmsUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/superadmin")
public class SuperAdminController {

    @Autowired
    superAdminLoginService superAdminLoginService;

    @PostMapping("/login")
    public ResponseEntity<?> SuperAdminlogin (@RequestBody LoginDTO request)
    {
        try
        {
            Response response = superAdminLoginService.superAdminLogin(request);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            Response response = new Response();
            response.setRespCode("96");
            response.setRespDescription("Failed");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @PostMapping("/createUsers")
    public ResponseEntity<?> SuperAdminCreateUsers (@RequestBody tmsUser request)
    {
        try
        {
            Response response = new Response();
            tmsUser Users = superAdminLoginService.superAdminCreateUsers(request);
            response.setRespCode("00");
            response.setRespDescription("Success");
            response.setRespBody(Users);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            Response response = new Response();
            response.setRespCode("96");
            response.setRespDescription("Failed");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @GetMapping("/sgetAllInstitutions")
    public ResponseEntity<?> superGetAllInstitutions(){
        try {
            Response response = new Response();
            List<Institution> institutionList =  superAdminLoginService.superAdminViewInstitutions();
            response.setRespCode("00");
            response.setRespDescription("success");
            response.setRespBody(institutionList);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response();
            response.setRespCode("96");
            response.setRespDescription("Failed");
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }

    }

    @GetMapping("/sgetAllUsers")
    public ResponseEntity<?> superGetAllUsers(){
        try {
            Response response = new Response();
            List<tmsUser> userList =  superAdminLoginService.superAdminViewUsers();
            response.setRespCode("00");
            response.setRespDescription("success");
            response.setRespBody(userList);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response();
            response.setRespCode("96");
            response.setRespDescription("Failed");
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }

    }

    @PostMapping("/screateInstitutions")
    public ResponseEntity<?> SuperAdminCreateInstitution (@RequestBody Institution request)
    {
        try
        {
            Response response = new Response();
            Institution institution = superAdminLoginService.superAdminCreateInstitution(request);
            response.setRespCode("00");
            response.setRespDescription("Success");
            response.setRespBody(institution);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            Response response = new Response();
            response.setRespCode("96");
            response.setRespDescription("Failed");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @GetMapping("/institution/{id}")
    public ResponseEntity<?> superGetInstitutionsByID(@PathVariable Long id){
        try {
            Response response = new Response();
            Institution institution =  superAdminLoginService.superAdminGetInstitution(id);
            response.setRespCode("00");
            response.setRespDescription("success");
            response.setRespBody(institution);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response();
            response.setRespCode("96");
            response.setRespDescription("success");
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }

    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> superGetUserByID(@PathVariable Long id){
        try {
            Response response = new Response();
            tmsUser user =  superAdminLoginService.superAdminGetUser(id);
            response.setRespCode("00");
            response.setRespDescription("success");
            response.setRespBody(user);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response();
            response.setRespCode("96");
            response.setRespDescription("success");
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }

    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> superdeleteUser(@RequestBody DeleteUser request){
        try {
            Response response = new Response();
            superAdminLoginService.superAdminDeleteUsers(request);
            response.setRespCode("00");
            response.setRespDescription("success");
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response();
            response.setRespCode("96");
            response.setRespDescription("success");
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
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
