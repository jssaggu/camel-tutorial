package com.jss.camel.components.routes.saga;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.camel.model.rest.RestParamType.body;

@Component
@ConditionalOnProperty(name = "jss.camel.saga.enabled", havingValue = "true")
public class OrderController extends RouteBuilder {

    private final OrderManagerService orderManagerService;
    private final CreditService creditService;

    public OrderController(OrderManagerService orderManagerService, CreditService creditService) {
        this.orderManagerService = orderManagerService;
        this.creditService = creditService;
    }

    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .apiProperty("api.title", "Saga Order Creator")
                .apiProperty("api.version", "1.0")
                .apiContextListing(true)
        ;

        rest()
                .consumes("application/json").produces("application/json")
                .post("/orders")
                .responseMessage("201", "When Created")
                .description("Create a new order").type(OrderDto.class)
                .param().name("body").type(body).description("Payload for an Order")
                .endParam()
                .to("direct:buy");

        rest()
                .produces("application/json")
                .get("/databases")
                .route()
                .setBody(e -> {
                    Map<String, Object> databases = new HashMap<>();
                    databases.put("Orders", orderManagerService.getOrders());
                    databases.put("OrderStatus", orderManagerService.getOrderStatusMap());
                    databases.put("Customer Account", creditService.getCustomerAccount());
                    databases.put("Order Amount", creditService.getOrderAmount());
                    return databases;
                })
        ;
    }
}
