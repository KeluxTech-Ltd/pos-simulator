//package com.jayrush.springmvcrest.jwt.controller;
//
//
//import com.jayrush.springmvcrest.domain.Institution
//import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
//import com.jayrush.springmvcrest.domain.domainDTO.InstitutionDTO;
//import com.jayrush.springmvcrest.jwt.AuthenticationRequest;
//import com.jayrush.springmvcrest.jwt.JwtTokenUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//@Controller
//public class TokenAuthController {
//
//    @Qualifier("jwtservice")
//    @Autowired
//    UserDetailsService userDetailsService;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//
//    @Autowired
//    JwtTokenUtil jwtTokenUtil;
//
//    @Autowired
//    AuthenticationManager authenticationManager;
//
//    @Autowired
//    InstitutionRepository institutionRepository;
//
//
//    @RequestMapping(value = "/auth",method = RequestMethod.POST)
//    public ResponseEntity<?> getAuthToken(@RequestBody AuthenticationRequest authenticationRequest){
//
//
//        // Reload password post-security so we can generate token
//        System.out.println("checking with "+authenticationRequest.getUsername());
//        InstitutionDTO institution = institutionRepository.findByinstitutionName(authenticationRequest.getUsername());
//        if(institution ==null){
//            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
//        }
//
//        System.out.println(authenticationRequest.getPassword()+" and "+institution.getPassword());
//
//        String newEncoded = passwordEncoder.encode(authenticationRequest.getPassword());
//        System.out.println(authenticationRequest.getPassword()+" and "+newEncoded);
//        if(!passwordEncoder.matches(authenticationRequest.getPassword(),institution.getPassword())){
//            System.out.println("does not matched");
//            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
//        }else{
//            System.out.println("matched");
//        }
//
//        final UserDetails userDetails = userDetailsService.loadUserByUsername(institution.getInstitutionName());
//        final String token = jwtTokenUtil.generateToken(userDetails, null);
//
//        // Return the token
//        return new ResponseEntity<>(token, HttpStatus.OK);
//    }
//}
