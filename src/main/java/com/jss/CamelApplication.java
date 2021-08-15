package com.jss;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @ComponentScan(basePackages = {"com.jss"})
 */
@SpringBootApplication
@EnableScheduling
public class CamelApplication {
    @Autowired
    ProducerTemplate producerTemplate;

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(CamelApplication.class, args);
    }
}
