package com.jss.camel.components.routes.rabbitmq;

import com.jss.camel.dto.WeatherDto;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.support.DefaultMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.camel.LoggingLevel.ERROR;
import static org.apache.camel.LoggingLevel.INFO;

@Component
@ConditionalOnProperty(name = "jss.camel.rabbitmq.enabled", havingValue = "true")
public class WeatherRoute extends RouteBuilder {
    public static final String EXCHANGE_WEATHER = "weather.direct";
    public static final String RABBIT_URI = "rabbitmq:" + EXCHANGE_WEATHER +"?queue=%s&routingKey=%s&autoDelete=false";
    public static final String QUEUE_WEATHER = "weather";
    public static final String QUEUE_WEATHER_EVENTS = "weather-events";

    @Override
    public void configure() throws Exception {

        //Called by Rabbit on message in weather queue
        /*
    {
     "city": "London",
     "temp": "20",
     "unit": "C"
    }
        */
        fromF(RABBIT_URI, QUEUE_WEATHER, QUEUE_WEATHER)
                .log(INFO, "Headers: ${headers}")
                .log(ERROR, "Before Enrichment: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, WeatherDto.class)
                .process(this::enrichWeatherDto)
                .log(ERROR, "After Enrichment: ${body}")
                .marshal().json(JsonLibrary.Jackson, WeatherDto.class)
                .toF(RABBIT_URI, QUEUE_WEATHER_EVENTS, QUEUE_WEATHER_EVENTS)
        //.to("file:///Users/jasvinder.saggu/projects/temp/camel-demos/?fileName=weather-events.txt&fileExist=Append")
        ;

        //all-node-provision
    }

    private void enrichWeatherDto(Exchange exchange) {
        WeatherDto dto = exchange.getMessage().getBody(WeatherDto.class);
        dto.setReceivedTime(new Date().toString());

        Message message = new DefaultMessage(exchange);
        message.setBody(dto);
        exchange.setMessage(message);
    }
}
