package com.jayrush.springmvcrest.Endpoints;

import com.jayrush.springmvcrest.Service.TerminalInterface;
import com.jayrush.springmvcrest.Service.TransactionInterface;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.Terminals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springfox.documentation.swagger2.mappers.ModelMapper;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(TerminalController.BASE_URL)
public class TerminalController {

    public static final String BASE_URL = "/api/v1/tms";
    private static final Logger logger = LoggerFactory.getLogger(TerminalController.class);


    @Autowired
    TerminalInterface terminalInterface;
    @Autowired
    ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<?> GetAllTerminals(){
        try {
            Response response = new Response();
            List<Terminals> terminalsList =  terminalInterface.getAllTerminals();
            response.setRespCode("00");
            response.setRespDescription("success");
            response.setRespBody(terminalsList);
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
    public ResponseEntity<?> GetTerminalByID(@PathVariable Long id){
        try {
            Response response = new Response();
            Terminals terminal =  terminalInterface.getTerminalByID(id);
            response.setRespCode("00");
            response.setRespDescription("success");
            response.setRespBody(terminal);
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
    public ResponseEntity<?> RegisterTerminal(@RequestBody Terminals terminals){
        try {
            Response response = new Response();
            Terminals terminal =  terminalInterface.RegisterTerminal(terminals);
            response.setRespCode("00");
            response.setRespDescription("success");
            response.setRespBody(terminal);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response();
            response.setRespCode("96");
            response.setRespDescription("Failed");
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }

    }
    @PutMapping("/{id}")
    public ResponseEntity<?> UpdateTerminals(@PathVariable Long id, @RequestBody Terminals terminals){
        try {
            Response response = new Response();
            Terminals t =  terminalInterface.EditTerminal(terminals);
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


    @PostMapping("/upload")
    public ResponseEntity<?> SaveUploadedTerminals(@RequestBody MultipartFile file){
        if (file.isEmpty()) {
            Response response = new Response();
            logger.info("Empty file");
            response.setRespCode("96");
            response.setRespDescription("Failed-Enpty File");
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        try {
            Response response = terminalInterface.uploadTerminals(file);
            response.setRespCode("00");
            response.setRespDescription("Success");
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response();
            logger.info(e.getMessage());
            response.setRespCode("96");
            response.setRespDescription("Failed");
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
