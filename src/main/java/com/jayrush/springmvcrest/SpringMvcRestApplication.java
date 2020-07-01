package com.jayrush.springmvcrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpringMvcRestApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringMvcRestApplication.class, args);
    }
}