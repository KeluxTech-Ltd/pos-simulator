package com.jayrush.springmvcrest.serviceProviders.controller;

import com.jayrush.springmvcrest.Repositories.bankServiceRepo;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.bank;
import com.jayrush.springmvcrest.domain.domainDTO.DeleteUser;
import com.jayrush.springmvcrest.serviceProviders.Models.profiles;
import com.jayrush.springmvcrest.serviceProviders.Models.serviceProviders;
import com.jayrush.springmvcrest.serviceProviders.dtos.profilesDTO;
import com.jayrush.springmvcrest.serviceProviders.services.serviceProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author JoshuaO
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(serviceProviderController.BASE_URL)
public class serviceProviderController {
    private static Logger logger = LoggerFactory.getLogger(serviceProviderController.class);

    public static final String BASE_URL = "/api/v1/serviceProviders";
    private static final String SUCCESS = "Success";
    private static final String SUCCESS_CODE = "00";
    private static final String FAILED = "Failed";
    private static final String FAILED_CODE = "96";

    @Autowired
    serviceProviderService serviceProviderService;
    @Autowired
    bankServiceRepo bankServiceRepo;

    @GetMapping()
    public ResponseEntity<?> getAllServiceProviders(){
        try {
            Response response = new Response();
            List<serviceProviders> providers = serviceProviderService.getAllProviders();
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(providers);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @GetMapping("/banks")
    public ResponseEntity<?> getAllBanks(){
        try {
            Response response = new Response();
            List<bank> banks = bankServiceRepo.findAll();
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(banks);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @GetMapping("/profiles")
    public ResponseEntity<?> getAllProfiles(){
        try {
            Response response = new Response();
            List<profiles> profiles = serviceProviderService.getAllProfiles();
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(profiles);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @PostMapping("/profilesbyProvidersID")
    public ResponseEntity<?> getAllProfilesbyServiceProviders(@RequestBody Long id){
        try {
            Response response = new Response();
            List<profiles> profiles = serviceProviderService.getAllProfilesbyProvidersID(id);
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(profiles);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @PostMapping("/addProviders")
    public ResponseEntity<?> addProviders(@RequestBody serviceProviders serviceProviders){
        try {
            Response response = new Response();
            response = serviceProviderService.addProvider(serviceProviders);
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @PostMapping("/addProfiles")
    public ResponseEntity<?> addProfiles(@RequestBody profiles profiles){
        try {
            Response response = new Response();
            serviceProviders profiles1 = serviceProviderService.addProfiles(profiles);

            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(profiles1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @PutMapping("/editprofiles/{id}")
    public ResponseEntity<?> editProfiles(@PathVariable Long id, @RequestBody profilesDTO profiles){
        try {
            Response response = new Response();
            profiles p = new profiles();
            p.setId(profiles.getId());
            p.setProfileIP(profiles.getProfileAddress());
            p.setProfileName(profiles.getProfileName());
            p.setPort(profiles.getPort());
            p.setZpk(profiles.getZpk());
            p.setServiceProviders(null);

            profiles profiles1 = serviceProviderService.editProfiles(p);
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(profiles1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @PutMapping("/providers/{id}")
    public ResponseEntity<?> editProviders(@PathVariable Long id, @RequestBody serviceProviders serviceProviders){
        try {
            Response response = new Response();
            serviceProviders providers = serviceProviderService.editProviders(serviceProviders);
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(providers);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @GetMapping("/profiles/{id}")
    public ResponseEntity<?> getProfilesbyID(@PathVariable Long id){
        try {
            Response response = new Response();
            profiles profiles = serviceProviderService.getProfilebyID(id);
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(profiles);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @GetMapping("/getserviceProvider/{id}")
    public ResponseEntity<?> getserviceProvidersbyID(@PathVariable Long id){
        try {
            Response response = new Response();
            serviceProviders serviceProviders = serviceProviderService.getProviderbyID(id);
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(serviceProviders);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @DeleteMapping("/deleteServiceProvider/{id}")
    public ResponseEntity<?> superDeleteProvider(@RequestBody DeleteUser request){
        try {
            Response response = serviceProviderService.deleteProvider(request);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response();
            response.setRespCode("96");
            response.setRespDescription("success");
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }

    }

    @DeleteMapping("/deleteProfile/{id}")
    public ResponseEntity<?> superDeleteProfile(@RequestBody DeleteUser request){
        try {
            Response response = serviceProviderService.deleteProfile(request);
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
