package com.jayrush.springmvcrest.commission.controller;

import com.jayrush.springmvcrest.commission.model.dtos.CommissionListDTO;
import com.jayrush.springmvcrest.commission.model.dtos.CommissionsHistoryDTO;
import com.jayrush.springmvcrest.commission.service.commissionService;
import com.jayrush.springmvcrest.domain.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author JoshuaO
 */

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(commissionController.BASE_URL)
public class commissionController {
    private static final Logger logger = LoggerFactory.getLogger(commissionController.class);
    public static final String BASE_URL = "/api/v1/commission";
    public static final String RESP_CODE = "00";
    public static final String SUCCESS = "success";
    public static final String RESP_CODE1 = "96";
    public static final String FAILED = "Failed";

    @Autowired
    commissionService commissionService;

    @PostMapping("/commissions")
    public ResponseEntity<?> GetAllCommissions(@RequestBody CommissionsHistoryDTO commissionsHistoryDTO){
        try {
            Response response = new Response();
            CommissionListDTO commissionListDTO =  commissionService.getCommissionHistory(commissionsHistoryDTO);
            response.setRespCode(RESP_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(commissionListDTO);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(RESP_CODE1);
            response.setRespDescription(FAILED);
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
