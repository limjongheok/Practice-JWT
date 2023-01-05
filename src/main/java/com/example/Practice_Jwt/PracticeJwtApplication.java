package com.example.Practice_Jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class PracticeJwtApplication {

    public static void main(String[] args) {

        SpringApplication.run(PracticeJwtApplication.class, args);
    }
}
