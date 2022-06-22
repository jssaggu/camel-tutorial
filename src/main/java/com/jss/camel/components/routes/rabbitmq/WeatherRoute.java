package com.jss.camel.components.routes.rabbitmq;

import com.jss.camel.dto.WeatherDto;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.support.DefaultMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Date;

import static org.apache.camel.LoggingLevel.DEBUG;
import static org.apache.camel.LoggingLevel.ERROR;
import static org.apache.camel.LoggingLevel.TRACE;

@Component
@ConditionalOnProperty(name = "jss.camel.rabbitmq.enabled", havingValue = "true")
public class WeatherRoute extends RouteBuilder {
    public static final String EXCHANGE_WEATHER = "weather.direct";
    public static final String RABBIT_URI = "rabbitmq:" + EXCHANGE_WEATHER +
            "?queue=%s&routingKey=%s&autoDelete=false" +
            "&concurrentConsumers=200" +
            "&threadPoolSize=200"
            //"&prefetchSize=0" +
            //"&prefetchCount=1" +
            //"&prefetchEnabled=true" +
            //"&prefetchGlobal=true"
            ;
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

        from("timer:orders?period=10")
                .process(this::createWeatherDto)
                .log(TRACE, "New Msg: ${body}")
                .marshal().json(JsonLibrary.Jackson, WeatherDto.class)
                .to("seda:complexProcess?multipleConsumers=true");

        from("seda:complexProcess?multipleConsumers=true")
                .toF(RABBIT_URI, QUEUE_WEATHER, QUEUE_WEATHER);

        fromF(RABBIT_URI, QUEUE_WEATHER, QUEUE_WEATHER)
//        from("direct:foo")
                .log(DEBUG, "Read from Rabbit: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, WeatherDto.class)
                .process(this::enrichWeatherDto)
//                .log(ERROR, "After Enrichment: ${body}")
                .marshal().json(JsonLibrary.Jackson, WeatherDto.class)
                .process(p -> {
//                    System.out.println("JSS-----------");
//                    System.out.println(p.getAllProperties());
//                    System.out.println(p.getContext().getExecutorServiceManager().getDefaultThreadPoolProfile());
//                    System.out.println("JSS-----X-----");
                })
                .log(DEBUG, "Starting http")
                .to("rest:get:hello?host=localhost:8080&throwExceptionOnFailure=true&okStatusCodeRange=100-499&httpClientConfigurer=ipsRestHttpClientConfigurer")
                .log(DEBUG, "Done http ${body}")
        ;
        //.toF(RABBIT_URI, QUEUE_WEATHER_EVENTS, QUEUE_WEATHER_EVENTS)
        //.to("file:///Users/jasvinder.saggu/projects/temp/camel-demos/?fileName=weather-events.txt&fileExist=Append")

        //all-node-provision
    }

    static int id = 1;

    private void createWeatherDto(Exchange exchange) {
        int idT = id++;
        WeatherDto dto = new WeatherDto();
        dto.setId(idT);
        dto.setCity(idT + " City");
        dto.setReceivedTime(new Date().toString());

        Message message = new DefaultMessage(exchange);
        message.setBody(dto);
        exchange.setMessage(message);
    }

    private void enrichWeatherDto(Exchange exchange) {
        WeatherDto dto = exchange.getMessage().getBody(WeatherDto.class);
        dto.setReceivedTime(new Date().toString());

        Message message = new DefaultMessage(exchange);
        message.setBody(dto);
        exchange.setMessage(message);
    }
}
