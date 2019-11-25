package com.jayrush.springmvcrest.config;
//
//import com.google.common.collect.Lists;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.*;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spi.service.contexts.SecurityContext;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger.web.SecurityConfiguration;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static springfox.documentation.builders.PathSelectors.regex;
//
//@EnableSwagger2
//@Configuration
//public class SwaggerConfig {
//    @Bean
//    public Docket productApi(){
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.jayrush.springmvcrest"))
//                .paths(regex("/api.*"))
//                .build()
//                .apiInfo(metaInfo())
//                .securitySchemes(Lists.newArrayList(apiKey()))
//                .securityContexts(Arrays.asList(securityContext()));
//    }
//
//
//
//    private ApiInfo metaInfo() {
//        ApiInfo info = new ApiInfo(
//                "Gbengus",
//                "TMS Terminal and Institution APIs",
//                "1.0",
//                "Terms of Service",
//                new Contact("3Line", "http://3lineng.com", "joshua.omonigho@3lineng.com"),
//                "Apache License Version 1.0",
//                "http://www.apache.org/license.html"
//        );
//        return info;
//    }
//
//    @Bean
//    public SecurityConfiguration security() {
//        return SecurityConfigurationBuilder.builder().scopeSeparator(",")
//                .additionalQueryStringParams(null)
//                .useBasicAuthenticationWithAccessCodeGrant(false).build();
//    }
//
//
//    private ApiKey apiKey() {
//        return new ApiKey("apiKey", "Authorization", "header");
//    }
//
//    private SecurityContext securityContext() {
//        return SecurityContext.builder().securityReferences(defaultAuth())
//                .forPaths(PathSelectors.any()).build();
//    }
//
//    private List<SecurityReference> defaultAuth() {
//        AuthorizationScope authorizationScope = new AuthorizationScope(
//                "global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        return Arrays.asList(new SecurityReference("apiKey",
//                authorizationScopes));
//    }
//
//}


import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
@ComponentScan(basePackages = {"com.jayrush.springmvcrest"})
public class SwaggerConfig {

    public static final Contact DEFAULT_CONTACT = new Contact(
            "3Line Software Dev", "http://www.3lineng.com", "softwaredev@3lineng.com");

    public static final ApiInfo DEFAULT_API_INFO = new ApiInfo(
            "TMS Terminal and Institution APIs", "This APIs are for the consumption by mobile/web devices", "1.0",
            "urn:tos", DEFAULT_CONTACT,
            "3Line", "http://www.3lineng.com", Collections.EMPTY_LIST);

    private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES =
            new HashSet<String>(Arrays.asList("application/json",
                    "application/xml"));

    //Change routePrefix of SwaggerUIOptions parameter, default is "swagger"

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()

//                .apis(RequestHandlerSelectors.basePackage(""))
                .paths(regex("/api/.*"))
                .build()
                .apiInfo(DEFAULT_API_INFO)
                .securitySchemes(Lists.newArrayList(apiKey()))
                .securityContexts(Arrays.asList(securityContext()));
        // .produces(DEFAULT_PRODUCES_AND_CONSUMES)
        // .consumes(DEFAULT_PRODUCES_AND_CONSUMES)


    }

    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder().scopeSeparator(",")
                .additionalQueryStringParams(null)
                .useBasicAuthenticationWithAccessCodeGrant(false).build();
    }


    private ApiKey apiKey() {
        return new ApiKey("apiKey", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth())
                .forPaths(PathSelectors.any()).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope(
                "global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("apiKey",
                authorizationScopes));
    }

}
