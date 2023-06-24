package com.jss.routes.rest;

import static org.apache.camel.model.rest.RestParamType.body;
import static org.apache.camel.model.rest.RestParamType.path;

import com.jss.dto.WeatherDto;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * This component is used to test Rest DSL. This service uses in-memory structure to manage the
 * data.
 */
@Component
@ConditionalOnProperty(name = "jss.camel.rest-dsl.enabled", havingValue = "true")
public class RestDsl extends RouteBuilder {

    private final RestDslService restDslService;

    public RestDsl(RestDslService restDslService) {
        this.restDslService = restDslService;
    }

    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "Saggu.UK Camel Rest APIs")
                .apiProperty("api.version", "1.0");

        rest().consumes("application/json")
                .produces("application/json")
                .get("/weather/{city}")
                .responseMessage("200", "On good request")
                .responseMessage("404", "For invalid requests")
                .description("Get weather data for a given city")
                .param()
                .name("city")
                .type(path)
                .description("The name of the city e.g. London")
                .dataType("string")
                .endParam()
                .outType(WeatherDto.class)
                .to("direct:get-weather-data")
                .post("/weather")
                .responseMessage("201", "When Created")
                .description("Add weather for a city")
                .type(WeatherDto.class)
                .param()
                .name("body")
                .type(body)
                .description("Payload for Weather")
                .endParam()
                .to("direct:save-weather-data");

        from("direct:get-weather-data").bean(restDslService, "getWeatherDataAndSetToExchange");

        from("direct:save-weather-data").bean(restDslService, "saveWeatherData").end();
    }
}
