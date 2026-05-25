package com.dbp.democarpultec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DemoCarpultecApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoCarpultecApplication.class, args);
    }

}
