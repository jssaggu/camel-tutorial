package com.jss.camel.components.routes.jms;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import static org.apache.camel.LoggingLevel.INFO;

@Component
@Slf4j
public class JMSRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("jms:queue:orders")
                .log(INFO, "Got a message: ${body}")
                .choice()
                    .when(e -> e.getMessage().getBody().toString().contains("card"))
                        .wireTap("jms:queue:fraud-check-messages")
                .end()
        ;
    }
}