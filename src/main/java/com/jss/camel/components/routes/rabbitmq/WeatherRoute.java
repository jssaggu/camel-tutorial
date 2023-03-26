package com.jss.camel.components.routes.rabbitmq;

import static org.apache.camel.LoggingLevel.ERROR;
import static org.apache.camel.LoggingLevel.INFO;

import com.jss.camel.dto.WeatherDto;
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
    public static final String EXCHANGE_WEATHER = "weather.direct";
    public static final String RABBIT_URI =
            "rabbitmq:" + EXCHANGE_WEATHER + "?queue=%s&routingKey=%s&autoDelete=false";
    public static final String QUEUE_WEATHER = "weather";
    public static final String QUEUE_WEATHER_EVENTS = "weather-events";

    @Override
    public void configure() throws Exception {

        // Called by Rabbit on message in weather queue
        /*
        {
         "city": "London",
         "temp": "20",
         "unit": "C"
        }
            */
        fromF(RABBIT_URI, QUEUE_WEATHER, QUEUE_WEATHER)
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
         * The following queue can be used to update the Weathe route. Simply send one the following
         * to weather-command route: START / STOP / RESUME / SUSPEND
         */
        fromF(RABBIT_URI, "weather-command", "weather-command")
                .process(
                        p -> {
                            String command = p.getMessage().getBody(String.class).toUpperCase();

                            System.out.println("Request for Weather Route: " + command);
                            DefaultCamelContext context = (DefaultCamelContext) getContext();

                            if (command.equals("SUSPEND")) {
                                context.suspendRoute("weather");
                            } else if (command.equals("RESUME")) {
                                context.resumeRoute("weather");
                            } else if (command.equals("STOP")) {
                                context.stopRoute("weather");
                            } else if (command.equals("START")) {
                                context.startRoute("weather");
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
