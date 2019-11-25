package com.jayrush.springmvcrest.Endpoints;

import com.jayrush.springmvcrest.Service.TerminalInterface;
import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.Terminals;
import com.jayrush.springmvcrest.domain.domainDTO.PagedInstitutionRequestDTO;
import com.jayrush.springmvcrest.domain.domainDTO.PagedRequestDTO;
import com.jayrush.springmvcrest.domain.domainDTO.TerminalListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.swagger2.mappers.ModelMapper;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(TerminalController.BASE_URL)
public class TerminalController {

    static final String BASE_URL = "/api/v1/tms";
    private static final Logger logger = LoggerFactory.getLogger(TerminalController.class);
    private static final String SUCCESS = "Success";
    private static final String FAILED = "Failed";
    private static final String SUCCESS_CODE = "00";
    private static final String FAILED_CODE = "96";

    @Autowired
    TerminalInterface terminalInterface;
    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/terminals")
//    @GetMapping()
    public ResponseEntity<?> getAllTerminals(@RequestBody PagedRequestDTO pagedTerminalsDTO){
//    public ResponseEntity<?> getAllTerminals(){
        try {
            Response response = new Response();
            TerminalListDTO terminalsList =  terminalInterface.getPagenatedTerminals(pagedTerminalsDTO);
//            List<Terminals> terminalsList =  terminalInterface.getAllTerminals();
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(terminalsList);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }

    }

    @PostMapping("/Institutionterminals")
//    @GetMapping()
    public ResponseEntity<?> getAllTerminalsByInstitutionID(@RequestBody PagedInstitutionRequestDTO institution){
        try {
            Response response = new Response();
            TerminalListDTO terminalsList =  terminalInterface.getPagenatedTerminalsByInstitution(institution);

            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(terminalsList);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTerminalByID(@PathVariable Long id){
        try {
            Response response = new Response();
            Terminals terminal =  terminalInterface.getTerminalByID(id);
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(terminal);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }

    }
    @PostMapping()
    public ResponseEntity<?> registerTerminal(@RequestBody Terminals terminals){
        try {
            Response response = new Response();
            Terminals terminal =  terminalInterface.RegisterTerminal(terminals);
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(terminal);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }

    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTerminals(@PathVariable Long id, @RequestBody Terminals terminals){
        try {
            Response response = new Response();
            Terminals t =  terminalInterface.EditTerminal(terminals);
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(t);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }

    }


    @PostMapping("/upload")
    public ResponseEntity<?> saveUploadedTerminals(@RequestBody MultipartFile file){
        if (file.isEmpty()) {
            Response response = new Response();
            logger.info("Empty file");
            response.setRespCode(FAILED_CODE);
            response.setRespDescription("Failed-Enpty File");
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        try {
            Response response = terminalInterface.uploadTerminals(file);
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response();
            logger.info(e.getMessage());
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
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
