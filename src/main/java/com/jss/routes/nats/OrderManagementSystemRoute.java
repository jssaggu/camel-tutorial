package com.jss.routes.nats;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import static org.apache.camel.LoggingLevel.INFO;

@Component
public class OrderManagementSystemRoute extends RouteBuilder {
    private static final String NATS_SERVER = "?servers=localhost:4222";

    @Override
    public void configure() throws Exception {

        from("nats:ORDERS.CHECKOUT?" + NATS_SERVER)
                .log(LoggingLevel.INFO, "CHECKOUT for ${body}")
                .to("nats:ORDERS.ORDER-CREATED" + NATS_SERVER)
        ;

        from("nats:ORDERS.ORDER-CREATED?" + NATS_SERVER)
                .log(LoggingLevel.INFO, "ORDER-CREATED for ${body}")
                .to("nats:ORDERS.PAYMENT-RECEIVED" + NATS_SERVER)
        ;

        from("nats:ORDERS.PAYMENT-RECEIVED?" + NATS_SERVER)
                .log(LoggingLevel.INFO, "PAYMENT-RECEIVED for ${body}")
                .to("nats:ORDERS.DELIVERED" + NATS_SERVER)
        ;

        from("nats:ORDERS.DELIVERED?" + NATS_SERVER)
                .log(LoggingLevel.INFO, "ORDER DELIVERED for ${body}")
        ;
    }
}
