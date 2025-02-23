package com.jss.routes.rabbitmq;

import com.jss.dto.WeatherDto;
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

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jss.routes.rabbitmq.RabbitmqConfiguration.QUEUE_WEATHER_DATA;
import static com.jss.routes.rabbitmq.RabbitmqConfiguration.QUEUE_WEATHER_EVENTS;
import static com.jss.routes.rabbitmq.RabbitmqConfiguration.RABBIT_URI;
import static com.jss.routes.rabbitmq.RabbitmqConfiguration.WEATHER_COMMAND;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.camel.LoggingLevel.DEBUG;

/**
 * This route is used to stress test RabbitMQ
 * Enable/disable "jss.camel.rabbitmq-stress-tester.enabled" property to run this route
 */
@Component
@ConditionalOnProperty(name = "jss.camel.rabbitmq-stress-tester.enabled", havingValue = "true")
public class RabbitmqStressTesterRoute extends RouteBuilder {

    AtomicInteger counter = new AtomicInteger(0);
    AtomicInteger tps = new AtomicInteger(0);
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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

        from("timer:time?period=1")
                .process(p -> {
                    p.getMessage().setBody("{\"city\":\"London\",\"temp\":\"20\",\"unit\":\"C\"}");
                })
                .toF(RABBIT_URI, QUEUE_WEATHER_DATA, QUEUE_WEATHER_DATA)
        ;

        from("timer:time?period=1")
            .process(p -> {
                    p.getMessage().setBody("{\"city\":\"London\",\"temp\":\"20\",\"unit\":\"C\"}");
                })
            .toF(RABBIT_URI, QUEUE_WEATHER_DATA, QUEUE_WEATHER_DATA)
        ;

        fromF(RABBIT_URI, QUEUE_WEATHER_DATA, QUEUE_WEATHER_DATA)
                .routeId("weather")
                .log(DEBUG, "Headers: ${headers}")
                .log(DEBUG, "Before Enrichment: ${body}")
                .unmarshal()
                .json(JsonLibrary.Jackson, WeatherDto.class)
                .process(this::enrichWeatherDto)
                .log(DEBUG, "After Enrichment: ${body}")
                .marshal()
                .json(JsonLibrary.Jackson, WeatherDto.class)
                .toF(RABBIT_URI, QUEUE_WEATHER_EVENTS, QUEUE_WEATHER_EVENTS);

        fromF(RABBIT_URI, QUEUE_WEATHER_EVENTS, QUEUE_WEATHER_EVENTS)
                .process(e -> {
                    e.getMessage().getBody();
                    counter.incrementAndGet();
                });

        /**
         * The following queue can be used to update the Weather route. Simply send one the
         * following to weather-command route: START / STOP / RESUME / SUSPEND
         */
        fromF(RABBIT_URI, WEATHER_COMMAND, WEATHER_COMMAND)
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

        //Second Printer
        scheduler.scheduleAtFixedRate(() -> {
                    int tpsNow = counter.get() - tps.get();
                    tps.set(counter.get());
                    System.out.println("[" + new Date() + "] [Total: " + counter.get() + "] [TPS: " + tpsNow + "]");
                },
                0, 1, SECONDS);
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
