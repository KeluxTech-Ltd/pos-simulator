package com.jayrush.springmvcrest.Endpoints;

import com.jayrush.springmvcrest.Repositories.UserRepository;
import com.jayrush.springmvcrest.Service.TransactionInterface;
import com.jayrush.springmvcrest.Service.tmsInstitutionService;
import com.jayrush.springmvcrest.Service.tmsLoginServices;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.domainDTO.LoginDTO;
import com.jayrush.springmvcrest.domain.tmsUser;
import com.jayrush.springmvcrest.domain.domainDTO.institutionTranRequestDTO;
import com.jayrush.springmvcrest.jwt.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1")
@RestController()
public class InstitutionLoginController {
    @Autowired
    tmsInstitutionService tmsInstitutionService;
    @Autowired
    tmsLoginServices tmsLoginService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TransactionInterface transactionInterface;

    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(InstitutionLoginController.class);
    private static final String SUCCESS  = "Success";
    private static final String FAILED  = "Failed";
    private static final String SUCCESS_CODE  = "00";
    private static final String FAILED_CODE  = "96";


    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody  LoginDTO request)
    {
        try
        {
            Response response = tmsLoginService.login(request);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        }
    }

    @PostMapping("/CreateInstitutionUser")
    public ResponseEntity<?> create(@RequestBody tmsUser request)
    {
        try
        {
            Response response = tmsLoginService.CreateInstitutionUser(request);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        }
    }

    @PostMapping("/InstitutionTransactions")
    public ResponseEntity<?> institutionsTransactionHistory(@RequestBody institutionTranRequestDTO request){
        try {
            Response response = new Response();
            String username = jwtTokenUtil.getUsernameFromToken(request.getAuthToken());
            tmsUser user = userRepository.findByusername(username);

            if (user!=null){
                String id = user.getInstitution().getInstitutionID();
                List<TerminalTransactions> transactions = transactionInterface.getTransactionsByinstitutionID(id);
                response.setRespBody(transactions);
                response.setRespDescription(SUCCESS);
                response.setRespCode(SUCCESS_CODE);
                return new ResponseEntity<>(response,HttpStatus.OK);
            }
            else{
                response.setRespCode(FAILED_CODE);
                response.setRespDescription(FAILED);
                response.setRespBody(null);
                return new ResponseEntity<>(response,HttpStatus.OK);
            }


        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
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
