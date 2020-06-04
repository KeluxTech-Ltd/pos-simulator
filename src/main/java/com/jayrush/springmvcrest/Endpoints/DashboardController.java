package com.jayrush.springmvcrest.Endpoints;

import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.Repositories.TerminalRepository;
import com.jayrush.springmvcrest.Repositories.TransactionRepository;
import com.jayrush.springmvcrest.Service.TransactionInterface;
import com.jayrush.springmvcrest.Service.dashboardInterface;
import com.jayrush.springmvcrest.domain.*;
import com.jayrush.springmvcrest.domain.domainDTO.TransactionHistoryDTO;
import com.jayrush.springmvcrest.domain.domainDTO.activeInstitutionDTO;
import com.jayrush.springmvcrest.slf4j.Logger;
import com.jayrush.springmvcrest.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JoshuaO
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(DashboardController.BASE_URL)
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    public static final String BASE_URL = "/api/v1/dashboard";
    public static final String SUCCESS = "Success";
    public static final String RESP_CODE = "00";
    public static final String FAILED = "Failed";
    public static final String RESP_CODE1 = "96";

    @Autowired
    TransactionInterface transactionInterface;
    @Autowired
    InstitutionRepository institutionRepository;
    @Autowired
    TerminalRepository terminalRepository;
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    dashboardInterface dashboardInterface;


    @PostMapping("/DashboardUtilities")
    public ResponseEntity<?> DashboardUtilities(@RequestBody String token){
        try{
            Response response = new Response();
            DashboardUtils dashboardUtils = dashboardInterface.getDashboardUtils(token);
            response.setRespBody(dashboardUtils);
            response.setRespCode(RESP_CODE);
            response.setRespDescription(SUCCESS);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode("96");
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response, HttpStatus.OK);

        }
    }

//    @GetMapping("topFiveActiveInstitutions")
//    public ResponseEntity<?> topFiveActiveInstitutions(){
//        try{
//            Response response = new Response();
//            Institution institution;
//            List<activeInstitutionDTO> activeInstitutionDTO = new ArrayList<>();
//
//            List<topFiveInstitutionDTO>list= transactionInterface.getTopFiveInstitutions();
//            for (int i = 0;i<list.size();i++){
//                activeInstitutionDTO activeInstitutionDTO1 = new activeInstitutionDTO();
//                String value = list.get(0).getInstitutionID().get(i+1);
//                institution = institutionRepository.findByInstitutionID(value);
//                activeInstitutionDTO1.setInstitutionName(institution.getInstitutionName());
//
//                activeInstitutionDTO.add(i,activeInstitutionDTO1);
//            }
//            response.setRespBody(activeInstitutionDTO);
//            response.setRespCode(RESP_CODE);
//            response.setRespDescription(SUCCESS);
//            return new ResponseEntity<>(response,HttpStatus.OK);
//        }catch (Exception e) {
//            logger.info(e.getMessage());
//            Response response = new Response();
//            response.setRespCode("96");
//            response.setRespDescription(FAILED);
//            response.setRespBody(null);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//
//        }
//    }
//    @GetMapping("getTotalInstitutions")
//    public ResponseEntity<?> getTotalInstitutions(){
//        try{
//            Response response = new Response();
//            response.setRespBody(transactionInterface.getTotalInstitutions());
//            response.setRespCode(RESP_CODE);
//            response.setRespDescription(SUCCESS);
//            return new ResponseEntity<>(response,HttpStatus.OK);
//        }catch (Exception e) {
//            logger.info(e.getMessage());
//            Response response = new Response();
//            response.setRespCode("96");
//            response.setRespDescription(FAILED);
//            response.setRespBody(null);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//
//        }
//    }
//    @GetMapping("transationStatistics")
//    public ResponseEntity<?> transationStatistics(){
//        try{
//            Response response = new Response();
//            response.setRespBody(transactionInterface.transactionStats());
//            response.setRespCode(RESP_CODE);
//            response.setRespDescription(SUCCESS);
//            return new ResponseEntity<>(response,HttpStatus.OK);
//        }catch (Exception e) {
//            logger.info(e.getMessage());
//            Response response = new Response();
//            response.setRespCode(RESP_CODE1);
//            response.setRespDescription(FAILED);
//            response.setRespBody(null);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//
//        }
//    }
//    @GetMapping("terminalCount")
//    public ResponseEntity<?> terminalCount(){
//        try{
//            Response response = new Response();
//            response.setRespBody(transactionInterface.terminalCount());
//            response.setRespCode(RESP_CODE);
//            response.setRespDescription(SUCCESS);
//            return new ResponseEntity<>(response,HttpStatus.OK);
//        }catch (Exception e) {
//            logger.info(e.getMessage());
//            Response response = new Response();
//            response.setRespCode("96");
//            response.setRespDescription(FAILED);
//            response.setRespBody(null);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//
//        }
//    }
//    @GetMapping("activeinactive")
//    public ResponseEntity<?> activeinactive(){
//        try{
//            Response response = new Response();
//            List<List<String>> activeTerminals = transactionInterface.activeInactiveTerminals();
//            activeInactive activeInactive = new activeInactive();
//            activeInactive.setActiveTerminals(activeTerminals.get(0).get(0));
//
//            List<Terminals> list = terminalRepository.findAll();
//            int totalTerminals = list.size();
//            int totalActive = Integer.parseInt(activeInactive.getActiveTerminals());
//            int totalInactive = totalTerminals-totalActive;
//
//            activeInactive.setActiveTerminals(String.valueOf(totalActive));
//            activeInactive.setInactiveTerminals(String.valueOf(totalInactive));
//
//            response.setRespBody(activeInactive);
//            response.setRespCode(RESP_CODE);
//            response.setRespDescription(SUCCESS);
//            return new ResponseEntity<>(response,HttpStatus.OK);
//
//        }catch (Exception e) {
//            logger.info(e.getMessage());
//            Response response = new Response();
//            response.setRespCode("96");
//            response.setRespDescription(FAILED);
//            response.setRespBody(null);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//
//        }
//    }
//
//    @PostMapping("/tranHistory")
//    public ResponseEntity<?> DashboardTransaction(@RequestBody TransactionHistoryDTO transactionHistory){
//        try{
//            Response response = new Response();
//            response.setRespBody(transactionInterface.getTransactionHistory(transactionHistory));
//            response.setRespCode("00");
//            response.setRespDescription("Success");
//            return new ResponseEntity<>(response,HttpStatus.OK);
//        }catch (Exception e) {
//            logger.info(e.getMessage());
//            Response response = new Response();
//            response.setRespCode("96");
//            response.setRespDescription("Failed");
//            response.setRespBody(null);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//
//        }
//    }

    @RequestMapping(
            value = "/**",
            method = RequestMethod.OPTIONS
    )
    public ResponseEntity handle() {
        return new ResponseEntity(HttpStatus.OK);
    }
}
