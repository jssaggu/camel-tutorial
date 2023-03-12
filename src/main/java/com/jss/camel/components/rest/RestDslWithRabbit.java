package com.jss.camel.components.rest;

import com.jss.camel.dto.WeatherDto;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.jss.camel.components.routes.rabbitmq.WeatherRoute.RABBIT_URI;

/**
 * This component is used to test Rest DSL and RabbitMQ together.
 * Client can invoke the GET and POST operations on the following URI: http://localhost:8080/services/weather/{city}
 * <p>
 * POST will store data in memory and will also publish an event to RabbitMQ weather-data queue.
 */
@Component
@ConditionalOnProperty(name = "jss.camel.rabbitmq.enabled", havingValue = "true")
public class RestDslWithRabbit extends RouteBuilder {

    private final WeatherDataProvider weatherDataProvider;

    public RestDslWithRabbit() {
        this.weatherDataProvider = new WeatherDataProvider();
    }

    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.auto);

        rest()
                .consumes("application/json").produces("application/json")
                .get("/weather/{city}").outType(WeatherDto.class).to("direct:get-weather-data")
                .post("/weather").type(WeatherDto.class).to("direct:save-weather-data");

        from("direct:get-weather-data")
                .process(this::getWeatherDataAndSetToExchange)
        ;

        from("direct:save-weather-data")
                .process(this::saveWeatherData)
                .wireTap("direct:write-to-rabbit")
                .end()
        ;

        from("direct:write-to-rabbit")
                .marshal().json(JsonLibrary.Jackson, WeatherDto.class)
                .toF(RABBIT_URI, "weather-data", "weather-data");
    }

    private void saveWeatherData(Exchange exchange) {
        try {
            WeatherDto body = exchange.getMessage().getBody(WeatherDto.class);
            this.weatherDataProvider.setCurrentWeather(body);
        } catch (Exception e) {
            log.error("Error While Saving. ", e);
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
        }
    }

    private void getWeatherDataAndSetToExchange(Exchange exchange) {
        RestDslService.getCity(exchange, this.weatherDataProvider);
    }
}
