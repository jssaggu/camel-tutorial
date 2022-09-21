package com.jss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @ComponentScan(basePackages = {"com.jss"})
 */
@SpringBootApplication
@EnableScheduling
public class CamelApplication extends SpringBootServletInitializer {
    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(CamelApplication.class, args);
    }
}
