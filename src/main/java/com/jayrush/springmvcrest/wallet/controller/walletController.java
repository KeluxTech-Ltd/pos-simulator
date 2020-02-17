package com.jayrush.springmvcrest.wallet.controller;

import com.jayrush.springmvcrest.Endpoints.InstitutionController;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.slf4j.Logger;
import com.jayrush.springmvcrest.slf4j.LoggerFactory;
import com.jayrush.springmvcrest.wallet.models.dtos.walletAccountdto;
import com.jayrush.springmvcrest.wallet.models.walletAccount;
import com.jayrush.springmvcrest.wallet.service.walletServices;
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
@RequestMapping(walletController.BASE_URL)
public class walletController {
    private static final Logger logger = LoggerFactory.getLogger(walletController.class);
    public static final String BASE_URL = "/api/v1/walletAccount";
    public static final String RESP_CODE = "00";
    public static final String SUCCESS = "success";
    public static final String RESP_CODE1 = "96";
    public static final String FAILED = "Failed";

    @Autowired
    walletServices walletServices;

    @PostMapping("/createWallet")
    public ResponseEntity<?> CreateWallet(@RequestBody walletAccountdto walletAccountdto){
        try {
            Response response = new Response();
            walletAccount walletAccount =  walletServices.createWalletAccount(walletAccountdto);
            response.setRespCode(RESP_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(walletAccount);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(RESP_CODE1);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }

    }

    @GetMapping("/getWallets")
    public ResponseEntity<?> GetWallets(){
        try {
            Response response = new Response();
            List<walletAccount> walletAccountList =  walletServices.getWalletAccount();
            response.setRespCode(RESP_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(walletAccountList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(RESP_CODE1);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }

    }

    @PostMapping("/getWalletbyWalletNumber")
    public ResponseEntity<?> GetWalletByWalletNumber(@RequestBody String walletNumber){
        try {
            Response response = new Response();
            walletAccount walletAccount =  walletServices.getWalletAccountByWalletNumber(walletNumber);
            response.setRespCode(RESP_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(walletAccount);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(RESP_CODE1);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }

    }

    @PostMapping("/updateWalletAccount")
    public ResponseEntity<?> GetWalletByWalletNumber(@RequestBody walletAccountdto walletNumber){
        try {
            Response response = new Response();
            walletAccount walletAccount =  walletServices.updateWalletAccount(walletNumber);
            response.setRespCode(RESP_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(walletAccount);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
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
