package com.jss.routes.rabbitmq;

import com.jss.dto.WeatherDto;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** This component requires RabbitMQ. please ensure below property is enabled. */
@Component
@ConditionalOnProperty(name = "jss.camel.rabbitmq-throttler.enabled", havingValue = "true")
public class ThrottlerRoute extends RouteBuilder {
    public static final String EXCHANGE_IOT = "weather.direct";
    public static final String RABBIT_URI =
            "spring-rabbitmq:"
                    + EXCHANGE_IOT
                    + "?"
                    + "queue=%s&"
                    + "routingKey=%s&"
                    + "autoDelete=false&"
                    + "autoAck=false&"
                    + "concurrentConsumers=50";
    public static final String QUEUE_IOT = "weather";

    final AtomicInteger sent = new AtomicInteger();
    final AtomicInteger received = new AtomicInteger();

    @Override
    public void configure() throws Exception {

        // External Sender Simulation
        from("timer:iot?period=10")
                .process(
                        p -> {
                            WeatherDto dto =
                                    WeatherDto.builder()
                                            .id(sent.incrementAndGet())
                                            .city("London")
                                            .temp("2")
                                            .unit("C")
                                            .build();
                            p.getMessage().setBody(dto);

                            System.out.print(
                                    "\rSent [" + sent.get() + "] Received [" + received + "]");
                        })
                .marshal()
                .json(JsonLibrary.Jackson, WeatherDto.class)
                .toF(RABBIT_URI, QUEUE_IOT, QUEUE_IOT);

        // IOT Processor
        fromF(RABBIT_URI, QUEUE_IOT, QUEUE_IOT)
                .throttle(10)
                .unmarshal()
                .json(JsonLibrary.Jackson, WeatherDto.class)
                .process(p -> received.incrementAndGet());
    }
}
