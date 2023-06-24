package com.jss.routes.rest;

import static java.util.Objects.nonNull;
import static org.apache.camel.Exchange.HTTP_RESPONSE_CODE;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.jss.dto.WeatherDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.support.DefaultMessage;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RestDslService {

    private final WeatherDataProvider weatherDataProvider;

    public RestDslService() {
        this.weatherDataProvider = new WeatherDataProvider();
    }

    static void getCity(Exchange exchange, WeatherDataProvider weatherDataProvider) {
        String city = exchange.getMessage().getHeader("city", String.class);
        WeatherDto currentWeather = weatherDataProvider.getCurrentWeather(city);

        if (nonNull(currentWeather)) {
            Message message = new DefaultMessage(exchange.getContext());
            message.setBody(currentWeather);
            exchange.setMessage(message);
            log.info("Weather Data found for {}", city);
        } else {
            exchange.getMessage().setHeader(HTTP_RESPONSE_CODE, NOT_FOUND.value());
            log.warn("Weather Data not found for {}", city);
        }
    }

    public void saveWeatherData(Exchange exchange) {
        try {
            WeatherDto body = exchange.getMessage().getBody(WeatherDto.class);
            this.weatherDataProvider.setCurrentWeather(body);
        } catch (Exception e) {
            log.error("Error While Saving. ", e);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
        }
    }

    public void getWeatherDataAndSetToExchange(Exchange exchange) {
        getCity(exchange, this.weatherDataProvider);
    }
}
