package com.jss.routes.rest;

import com.jss.dto.WeatherDto;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jss.camel.rest-java-dsl.enabled", havingValue = "true")
public class RestJavaDsl extends RouteBuilder {

    private final WeatherDataProvider weatherDataProvider;
    private final RestDslService restDslService;

    public RestJavaDsl(RestDslService restDslService) {
        this.restDslService = restDslService;
        this.weatherDataProvider = new WeatherDataProvider();
    }

    @Override
    public void configure() throws Exception {
        from("rest:get:javadsl/weather/{city}?produces=application/json")
                .outputType(WeatherDto.class)
                .process(this::getWeatherDataAndSetToExchange);

        /** Method Post Payload Sample: { "city": "New Delhi", "temp": "48", "unit": "C" } */
        from("rest:post:javadsl/weather?consumes=application/json")
                .log(LoggingLevel.ERROR, "Body: ${body}")
                .unmarshal()
                .json(JsonLibrary.Jackson, WeatherDto.class)
                .bean(restDslService, "saveWeatherData");
    }

    private void getWeatherDataAndSetToExchange(Exchange exchange) {
        RestDslService.getCity(exchange, this.weatherDataProvider);
    }
}
