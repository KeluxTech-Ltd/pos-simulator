package com.jayrush.springmvcrest.jwt;

import com.jayrush.springmvcrest.jwt.service.JwtUserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);

    @Autowired
    @Qualifier("jwtservice")
    private JwtUserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

//    @Autowired
//    private LoggedInAgent loggedInAgent;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {


        String contentType = request.getContentType();

        logger.debug("Content Type "+ contentType);

        String authHeader = request.getHeader("Authorization");
        if(authHeader!=null) {
            logger.info("Auth header: {}", authHeader);
            logger.info("Request coming from  header: {}", request.getRemoteAddr());
            String authToken = authHeader.substring(7);
            String username = jwtTokenUtil.getUsernameFromToken(authToken);


            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {


                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(authToken, username)) {
                    logger.info("Valid authentication token");
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    logger.info("Authenticated user " + username + ", setting security context");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("User [{}] authenticated", username);
//                    loggedInAgent.setAgentId(username);
                }
            }
        }

            chain.doFilter(request, response);


    }
}