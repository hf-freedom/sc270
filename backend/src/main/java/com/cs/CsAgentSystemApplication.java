package com.cs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CsAgentSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(CsAgentSystemApplication.class, args);
    }
}
