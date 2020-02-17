package com.jayrush.springmvcrest.Endpoints;

import com.jayrush.springmvcrest.Repositories.UserRepository;
import com.jayrush.springmvcrest.Service.*;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.Terminals;
import com.jayrush.springmvcrest.domain.domainDTO.InstitutionUser;
import com.jayrush.springmvcrest.domain.domainDTO.LoginDTO;
import com.jayrush.springmvcrest.domain.tmsUser;
import com.jayrush.springmvcrest.domain.domainDTO.institutionTranRequestDTO;
import com.jayrush.springmvcrest.jwt.JwtTokenUtil;
import com.jayrush.springmvcrest.rolesPermissions.models.Permissions;
import com.jayrush.springmvcrest.rolesPermissions.repositories.permissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.jayrush.springmvcrest.domain.roleType.InstitutionAdmin;
import static com.jayrush.springmvcrest.domain.roleType.SuperAdmin;

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
    @Autowired
    UserService userService;
    @Autowired
    permissionRepository permissionRepository;

    @Autowired
    TerminalInterface terminalInterface;
    private static final Logger logger = LoggerFactory.getLogger(InstitutionLoginController.class);
    private static final String SUCCESS  = "Success";
    private static final String FAILED  = "Failed";
    private static final String SUCCESS_CODE  = "00";
    private static final String FAILED_CODE  = "96";

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody  LoginDTO request)
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
    public ResponseEntity<?> create (@RequestBody tmsUser request)
    {
        try
        {
            String username = jwtTokenUtil.getUsernameFromToken(request.getToken());
            tmsUser User = userRepository.findByusername(username);
            Permissions permissions = permissionRepository.findByName("CREATE_USER");

            if (Objects.nonNull(User)){
                Response response = new Response();

                if (User.getRole().getPermissions().contains(permissions)){
                    request.setInstitution(User.getInstitution());
                    response = tmsLoginService.CreateInstitutionUser(request);

                }
                return new ResponseEntity<>(response, HttpStatus.OK);

            }
            else{
                Response response = new Response();
                response.setRespCode(FAILED_CODE);
                response.setRespDescription("User not Admin/SuperAdmin");
                response.setRespBody(null);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

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

    @PostMapping("/InstitutionUserList")
    public ResponseEntity<?>GetInstitutionUsers(@RequestBody InstitutionUser user){
        try {
            Response response = new Response();
            String username = jwtTokenUtil.getUsernameFromToken(user.getToken());
            logger.info("Username from auth token is {}",username);
            tmsUser institutionID = userRepository.findByusername(username);
            List<tmsUser> User = userService.getInstitutionUsers(institutionID.getInstitution().getInstitutionID());
            if (Objects.nonNull(User)){
                response.setRespBody(User);
                response.setRespDescription(SUCCESS);
                response.setRespCode(SUCCESS_CODE);
            }
            else {
                response.setRespCode(FAILED_CODE);
                response.setRespDescription(FAILED);
                response.setRespBody(null);
            }
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        }
    }

    @PostMapping("/InstitutionTerminalList")
    public ResponseEntity<?>GetInstitutionTerminals(@RequestBody InstitutionUser user){
        try{
            Response response = new Response();
            String username = jwtTokenUtil.getUsernameFromToken(user.getToken());
            logger.info("Username from auth token is {}",username);
            tmsUser institutionID = userRepository.findByusername(username);
            List<Terminals> terminals = terminalInterface.getTerminalsbyInstitution(institutionID.getInstitution().getInstitutionID());
            if (Objects.nonNull(terminals)){
                response.setRespBody(terminals);
                response.setRespDescription(SUCCESS);
                response.setRespCode(SUCCESS_CODE);
            }
            else {
                response.setRespCode(FAILED_CODE);
                response.setRespDescription(FAILED);
                response.setRespBody(null);
            }
            return new ResponseEntity<>(response,HttpStatus.OK);
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
