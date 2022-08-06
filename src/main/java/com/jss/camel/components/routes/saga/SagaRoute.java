package com.jss.camel.components.routes.saga;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.saga.InMemorySagaService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConditionalOnProperty(name = "jss.camel.saga.enabled", havingValue = "true")
public class SagaRoute extends RouteBuilder {

    private final OrderManagerService orderManagerService;
    private final CreditService creditService;

    public SagaRoute(OrderManagerService orderManagerService, CreditService creditService) {
        this.orderManagerService = orderManagerService;
        this.creditService = creditService;
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class)
                .handled(false)
                .log(LoggingLevel.ERROR, "${exception}")
        ;

        //Step 1
        getContext().addService(new InMemorySagaService());

        from("direct:buy")
                .process(exchange -> exchange.getMessage().setHeader("id", UUID.randomUUID().toString()))
                .log(LoggingLevel.INFO, "Id: ${header.id}, Order Received: ${body}")
                .saga()
                .to("direct:newOrder")
                .to("direct:makePayment")
                .to("direct:shipOrder")
        ;

        from("direct:newOrder")
                .saga()
                .propagation(SagaPropagation.MANDATORY)
                .option("id", header("id"))
                .setBody(body())
                .compensation("direct:cancelOrder")
                .bean(orderManagerService, "newOrder")
                .log("ID: ${header.id}, Order ${body} created");

        from("direct:makePayment")
                .bean(creditService, "makePayment")
                .log("ID: ${header.id}, Credit ${header.amount} reserved in action ${body}");

        from("direct:cancelOrder")
                .log("ID: ${header.id}, Order ${body} cancelling")
                .bean(orderManagerService, "cancelOrder")
                .log("ID: ${header.id}, Order ${body} Cancelled");

        from("direct:shipOrder")
                .saga()
                .propagation(SagaPropagation.MANDATORY)
                .option("id", header("id"))
                .option("body", body())
                .option("customerId", simple("${body.customerId}"))
                .compensation("direct:refundCredit")
                .bean(orderManagerService, "shipOrder")
                .log(LoggingLevel.INFO, "prepareOrder ${body}")
                .log("ID: ${header.id}, Order ${body} sent for shipping");

        // compensation
        from("direct:refundCredit")
                .bean(creditService, "refundCredit")
                .log("ID: ${header.id}, Refund action ${body} refunded");
    }
}
