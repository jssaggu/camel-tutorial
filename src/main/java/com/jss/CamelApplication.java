package com.jss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @ComponentScan(basePackages = {"com.jss"})
 */
@SpringBootApplication
@EnableScheduling
public class CamelApplication {
    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(CamelApplication.class, args);
    }
}
