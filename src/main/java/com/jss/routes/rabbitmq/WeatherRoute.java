package com.jss.routes.rabbitmq;

import static com.jss.routes.rabbitmq.RabbitmqConfiguration.QUEUE_WEATHER_DATA;
import static com.jss.routes.rabbitmq.RabbitmqConfiguration.QUEUE_WEATHER_EVENTS;
import static com.jss.routes.rabbitmq.RabbitmqConfiguration.RABBIT_URI;
import static org.apache.camel.LoggingLevel.ERROR;
import static org.apache.camel.LoggingLevel.INFO;

import com.jss.dto.WeatherDto;
import java.util.Date;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.engine.DefaultRoute;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.support.DefaultMessage;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** This route can be use to interact with RabbitMQ. */
@Component
@ConditionalOnProperty(name = "jss.camel.rabbitmq.enabled", havingValue = "true")
public class WeatherRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        log.info("JSS declaring route");

        // Called by Rabbit on message in weather queue
        /*
        {
         "city": "London",
         "temp": "20",
         "unit": "C"
        }
            */
        fromF(RABBIT_URI, QUEUE_WEATHER_DATA, QUEUE_WEATHER_DATA)
                .routeId("weather")
                .log(INFO, "Headers: ${headers}")
                .log(ERROR, "Before Enrichment: ${body}")
                .unmarshal()
                .json(JsonLibrary.Jackson, WeatherDto.class)
                .process(this::enrichWeatherDto)
                .log(ERROR, "After Enrichment: ${body}")
                .marshal()
                .json(JsonLibrary.Jackson, WeatherDto.class)
                .toF(RABBIT_URI, QUEUE_WEATHER_EVENTS, QUEUE_WEATHER_EVENTS)
        // .to("file:///Users/jasvinder.saggu/projects/temp/camel-demos/?fileName=weather-events.txt&fileExist=Append")
        ;

        /**
         * The following queue can be used to update the Weather route. Simply send one the
         * following to weather-command route: START / STOP / RESUME / SUSPEND
         */
        fromF(RABBIT_URI, "weather-command", "weather-command")
                .process(
                        p -> {
                            String command = p.getMessage().getBody(String.class).toUpperCase();

                            System.out.println("Request for Weather Route: " + command);
                            DefaultCamelContext context = (DefaultCamelContext) getContext();

                            switch (command) {
                                case "SUSPEND" -> context.suspendRoute("weather");
                                case "RESUME" -> context.resumeRoute("weather");
                                case "STOP" -> context.stopRoute("weather");
                                case "START" -> context.startRoute("weather");
                            }

                            DefaultRoute weather = (DefaultRoute) context.getRoute("weather");

                            System.out.println("State of Weather Route: " + weather.getStatus());
                        });

        // all-node-provision
    }

    private void enrichWeatherDto(Exchange exchange) {
        MDC.put("JSS", "Str-1");
        WeatherDto dto = exchange.getMessage().getBody(WeatherDto.class);
        dto.setReceivedTime(new Date().toString());

        Message message = new DefaultMessage(exchange);
        message.setBody(dto);
        exchange.setMessage(message);
    }
}
