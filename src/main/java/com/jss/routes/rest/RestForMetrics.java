package com.jss.routes.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jss.camel.rest-metrics.enabled", havingValue = "true")
public class RestForMetrics extends RouteBuilder {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure() throws Exception {
        restConfiguration().component("servlet").bindingMode(RestBindingMode.auto);

        rest().consumes("application/json")
                .produces("application/json")
                .get("/dates")
                .to("direct:rest-dates");

        from("direct:rest-dates")
                .routeId("Rest-Dates")
                .setBody(e -> "Date: " + new Date().toString());
    }
}
