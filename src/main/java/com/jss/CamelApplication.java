package com.jss;

import org.apache.camel.component.seda.ArrayBlockingQueueFactory;
import org.apache.camel.component.seda.LinkedBlockingQueueFactory;
import org.apache.camel.component.seda.PriorityBlockingQueueFactory;
import org.apache.camel.opentelemetry.starter.CamelOpenTelemetry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
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

    @Bean
    public LinkedBlockingQueueFactory linkedBlockingQueueFactory() {
        return new LinkedBlockingQueueFactory();
    }

    @Bean
    public ArrayBlockingQueueFactory arrayBlockingQueueFactory() {
        return new ArrayBlockingQueueFactory();
    }

    @Bean
    public PriorityBlockingQueueFactory priorityBlockingQueueFactory() {
        return new PriorityBlockingQueueFactory();
    }
}
