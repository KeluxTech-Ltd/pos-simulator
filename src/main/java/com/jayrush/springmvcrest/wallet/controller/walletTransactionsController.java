package com.jayrush.springmvcrest.wallet.controller;

import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.wallet.models.dtos.WalletTransactionHistoryDTO;
import com.jayrush.springmvcrest.wallet.models.dtos.WalletTransactionListDTO;
import com.jayrush.springmvcrest.wallet.service.walletTransactionServices;
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
@RequestMapping(walletTransactionsController.BASE_URL)
public class walletTransactionsController {
    private static final Logger logger = LoggerFactory.getLogger(walletTransactionsController.class);
    public static final String BASE_URL = "/api/v1/walletAccountTransactions";
    public static final String RESP_CODE = "00";
    public static final String SUCCESS = "success";
    public static final String RESP_CODE1 = "96";
    public static final String FAILED = "Failed";

    @Autowired
    walletTransactionServices walletTransactionServices;

    @PostMapping("/GetWalletTransactions")
    public ResponseEntity<?> WalletTransactions(@RequestBody WalletTransactionHistoryDTO WalletTransactionHistoryDTO){
        try {
            Response response = new Response();
            WalletTransactionListDTO walletTransaction =  walletTransactionServices.getWalletTransactionHistory(WalletTransactionHistoryDTO);
            response.setRespCode(RESP_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(walletTransaction);
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

    public ResponseEntity handle() {
        return new ResponseEntity(HttpStatus.OK);
    }

}
