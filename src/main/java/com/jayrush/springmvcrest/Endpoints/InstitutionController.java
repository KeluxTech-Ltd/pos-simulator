package com.jayrush.springmvcrest.Endpoints;

import com.jayrush.springmvcrest.Service.institutionservice;
import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.domainDTO.InstitutionListDTO;
import com.jayrush.springmvcrest.domain.domainDTO.PagedRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(InstitutionController.BASE_URL)
public class InstitutionController {
    public static final String BASE_URL = "/api/v1/institution";


    @Autowired
    institutionservice institutionService;

    @PostMapping("/institutions")
//    @GetMapping()
    public ResponseEntity<?> GetAllInstitutions(@RequestBody PagedRequestDTO pagedTerminalsDTO){
//    public ResponseEntity<?> GetAllInstitutions(){
        try {
            Response response = new Response();
            InstitutionListDTO institutionList =  institutionService.getPagenatedInstitutions(pagedTerminalsDTO);
//            List<Institution> institutionList =  institutionService.getAllInstitution();
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

    @GetMapping("/{id}")
    public ResponseEntity<?> GetInstitutionsByID(@PathVariable Long id){
        try {
            Response response = new Response();
            Institution institution =  institutionService.getinstitutionbyid(id);
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

    @PostMapping()
    public ResponseEntity<?> RegisterInstitution(@RequestBody Institution institution){
        try {
            Response response = new Response();
            institutionService.registerInstitution(institution);
            response.setRespCode("00");
            response.setRespDescription("success");
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response();
            response.setRespCode("96");
            response.setRespDescription("Failed");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> UpdateInstitution(@PathVariable Long id, @RequestBody Institution institution){
        try {
            Response response = new Response();
            Institution t =  institutionService.editInstitution(institution);
            response.setRespCode("00");
            response.setRespDescription("success");
            response.setRespBody(t);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response();
            response.setRespCode("96");
            response.setRespDescription("Failed");
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
