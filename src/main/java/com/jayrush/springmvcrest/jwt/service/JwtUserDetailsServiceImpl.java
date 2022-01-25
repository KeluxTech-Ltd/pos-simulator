package com.jayrush.springmvcrest.jwt.service;

import com.jayrush.springmvcrest.Repositories.UserRepository;
import com.jayrush.springmvcrest.domain.tmsUser;
import com.jayrush.springmvcrest.jwt.JwtUserFactory;
import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.domainDTO.InstitutionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service("jwtservice")
public class JwtUserDetailsServiceImpl implements UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(JwtUserDetailsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    private IpAddressUtils addressUtils;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        logger.info("Admin with ID [{}] logging in", username);
        tmsUser tmsUser = userRepository.findByusername(username);
        final String ip = "";//addressUtils.getClientIP();
        if (tmsUser == null) {
            logger.info("Could not find User name [{}]", username);
            throw new UsernameNotFoundException(String.format("No institution found with username '%s'.", username));
        } else {
            logger.info("Found institution [{}]", username);
            return JwtUserFactory.create(tmsUser, ip);
        }
    }


}
