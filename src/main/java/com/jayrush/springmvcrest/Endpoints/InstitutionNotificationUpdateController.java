package com.jayrush.springmvcrest.Endpoints;

import com.jayrush.springmvcrest.Repositories.TransactionRepository;
import com.jayrush.springmvcrest.Service.TransactionInterface;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.domainDTO.dateRange;
import com.jayrush.springmvcrest.slf4j.Logger;
import com.jayrush.springmvcrest.slf4j.LoggerFactory;
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
@RequestMapping(InstitutionNotificationUpdateController.BASE_URL)
public class InstitutionNotificationUpdateController {
    public static final String BASE_URL = "/api/v1/requery";
    private static final Logger logger = LoggerFactory.getLogger(InstitutionNotificationUpdateController.class);

    @Autowired
    TransactionInterface transactionInterface;



    @PostMapping("/repushTransactions")
    public ResponseEntity<?> repushTransactions(@RequestBody dateRange dateRange){
        try {
            Response response = new Response();
            List<TerminalTransactions> terminalTransactions =  transactionInterface.repushTransactions(dateRange);
            response.setRespCode("00");
            response.setRespDescription("success");
            response.setRespBody(terminalTransactions);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
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
