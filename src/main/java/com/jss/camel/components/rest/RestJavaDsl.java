package com.jss.camel.components.rest;

import com.jss.camel.dto.WeatherDto;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.DefaultMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.apache.camel.Exchange.HTTP_RESPONSE_CODE;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
@ConditionalOnProperty(name = "jss.camel.rest.enabled", havingValue = "true")
public class RestJavaDsl extends RouteBuilder {

    private final WeatherDataProvider weatherDataProvider;

    public RestJavaDsl() {
        this.weatherDataProvider = new WeatherDataProvider();
    }

    @Override
    public void configure() throws Exception {
        from("rest:get:javadsl/weather/{city}?produces=application/json")
                .outputType(WeatherDto.class)
                .process(this::getWeatherDataAndSetToExchange);
    }

    private void getWeatherDataAndSetToExchange(Exchange exchange) {
        RestDslService.getCity(exchange, this.weatherDataProvider);
    }
}
