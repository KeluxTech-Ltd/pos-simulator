package com.jayrush.springmvcrest.Endpoints;


import com.jayrush.springmvcrest.Service.TransactionInterface;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.domainDTO.TransactionHistoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(TransactionController.BASE_URL)
public class TransactionController {
    public static final String BASE_URL = "/api/v1/transactions";

    @Autowired
    TransactionInterface transactionInterface;


    @PostMapping("/tranHistory")
    ResponseEntity<?> transactionHistory(@RequestBody TransactionHistoryDTO transactionHistory){
        try{
            Response response = new Response();
            response.setRespBody(transactionInterface.getTransactionHistory(transactionHistory));
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
