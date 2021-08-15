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

    public RestJavaDsl(WeatherDataProvider weatherDataProvider) {
        this.weatherDataProvider = weatherDataProvider;
    }

    @Override
    public void configure() throws Exception {
        from("rest:get:javadsl/weather/{city}?produces=application/json")
                .outputType(WeatherDto.class)
                .process(this::getWeatherDataAndSetToExchange);
    }

    private void getWeatherDataAndSetToExchange(Exchange exchange) {
        String city = exchange.getMessage().getHeader("city", String.class);
        WeatherDto currentWeather = this.weatherDataProvider.getCurrentWeather(city);

        if (Objects.nonNull(currentWeather)) {
            Message message = new DefaultMessage(exchange.getContext());
            message.setBody(currentWeather);
            exchange.setMessage(message);
        } else {
            exchange.getMessage().setHeader(HTTP_RESPONSE_CODE, NOT_FOUND.value());
        }
    }
}
