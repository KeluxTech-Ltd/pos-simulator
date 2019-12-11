package com.jayrush.springmvcrest.Endpoints;

import com.jayrush.springmvcrest.Service.TransactionInterface;
import com.jayrush.springmvcrest.domain.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author JoshuaO
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(TransactionController.BASE_URL)
public class DashboardController {
    public static final String BASE_URL = "/api/v1/dashboard";

    @Autowired
    TransactionInterface transactionInterface;

    @GetMapping("/{id}")
    ResponseEntity<?> transactionHistory(@PathVariable Long id){
        try{
            Response response = new Response();
            response.setRespBody(transactionInterface.getTransactionByID(id));
            response.setRespCode("00");
            response.setRespDescription("Success");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (Exception e) {
            Response response = new Response();
            response.setRespCode("96");
            response.setRespDescription("Failed");
            response.setRespBody(null);
            return new ResponseEntity<>(response, HttpStatus.OK);

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
