package com.jss.routes.saga;

import java.util.UUID;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.saga.InMemorySagaService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

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

        // Step 1
        getContext().addService(new InMemorySagaService());

        from("direct:order")
                .process(
                        exchange -> {
                            exchange.getMessage().setHeader("id", UUID.randomUUID().toString());
                            OrderDto order = exchange.getMessage().getBody(OrderDto.class);
                            order.setOrderId(exchange.getMessage().getHeader("id", String.class));
                            exchange.getMessage().setBody(order);
                        })
                .log(LoggingLevel.INFO, "Id: ${header.id}, Order Received: ${body}")
                .saga()
                .to("direct:newOrder")
                .to("direct:makePayment")
                .to("direct:shipOrder");

        from("direct:newOrder")
                .saga()
                .propagation(SagaPropagation.MANDATORY)
                .option("id", header("id"))
                .setBody(body())
                .compensation("direct:cancelOrder")
                .bean(orderManagerService, "newOrder")
                .log("ID: ${header.id}, Order ${body} created");

        // compensation
        from("direct:cancelOrder")
                .log("ID: ${header.id}, Order ${body} cancelling")
                .bean(orderManagerService, "cancelOrder")
                .log("ID: ${header.id}, Order ${body} Cancelled");

        from("direct:makePayment")
                .saga()
                .propagation(SagaPropagation.MANDATORY)
                .option("id", header("id"))
                .option("body", body())
                .option("customerId", simple("${body.customerId}"))
                .compensation("direct:refundPayment")
                .bean(creditService, "makePayment");

        // compensation
        from("direct:refundPayment").bean(creditService, "refundPayment");

        from("direct:shipOrder")
                .saga()
                .propagation(SagaPropagation.MANDATORY)
                .option("id", header("id"))
                .option("body", body())
                .option("customerId", simple("${body.customerId}"))
                .compensation("direct:cancelShipping")
                .completion("direct:completeShipping")
                .bean(orderManagerService, "shipOrder");

        // compensation
        from("direct:cancelShipping").bean(orderManagerService, "cancelShipping");

        from("direct:completeShipping").bean(orderManagerService, "completeShipping");
    }
}
