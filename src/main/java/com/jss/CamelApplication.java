package com.jss;

import org.apache.camel.opentelemetry.starter.CamelOpenTelemetry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @ComponentScan(basePackages = {"com.jss"})
 */
@SpringBootApplication
@EnableScheduling
@CamelOpenTelemetry
public class CamelApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(CamelApplication.class, args);
    }
}
