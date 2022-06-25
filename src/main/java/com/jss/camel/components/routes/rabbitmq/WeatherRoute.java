package com.jss.camel.components.routes.rabbitmq;

import com.jss.camel.dto.WeatherDto;
import org.apache.activemq.util.JMSExceptionSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.support.DefaultMessage;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;
import java.util.Date;

import static org.apache.camel.LoggingLevel.DEBUG;
import static org.apache.camel.LoggingLevel.ERROR;
import static org.apache.camel.LoggingLevel.TRACE;

@Component
@ConditionalOnProperty(name = "jss.camel.rabbitmq.enabled", havingValue = "true")
public class WeatherRoute extends RouteBuilder {
    public static final int MESSAGE_SENDER_TIMER_MS = 10;
    public static final int SERVER_REST_SLEEP_SECONDS = 200000;

    public static final int RABBIT_CONCURRENT_CONSUMERS = 20;
    public static final int RABBIT_THREAD_POOL_SIZE = 20;

    public static final int REST_TIMEOUT_MS = 20_000;
    public static final int CONNECTION_MAX_TOTAL = 2;
    public static final int CONNECTION_MAX_PER_ROUTE = 1;

    public static final String EXCHANGE_WEATHER = "weather.direct";
    public static final String RABBIT_URI = "rabbitmq:" + EXCHANGE_WEATHER +
            "?queue=%s&routingKey=%s&autoDelete=false" +
            "&concurrentConsumers=" + RABBIT_CONCURRENT_CONSUMERS +
            "&threadPoolSize=" + RABBIT_THREAD_POOL_SIZE
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

        onException(Exception.class)
                .log(DEBUG, "JSS SocketTimeoutException: ${exception}")
                .handled(true);

        onException(SocketTimeoutException.class)
                .log(DEBUG, "JSS SocketTimeoutException: ${exception}")
                .handled(true);

        onException(ConnectionPoolTimeoutException.class)
                .log(ERROR, "JSS ConnectionPoolTimeoutException: ${exception}")
                .handled(false)
                .process(p -> System.exit(0))
        ;

        from("timer:orders?period=" + MESSAGE_SENDER_TIMER_MS)
                .process(this::createWeatherDto)
                .log(TRACE, "New Msg: ${body}")
                .marshal().json(JsonLibrary.Jackson, WeatherDto.class)
                .to("seda:complexProcess?multipleConsumers=true");

        from("seda:complexProcess?multipleConsumers=true")
                .toF(RABBIT_URI, QUEUE_WEATHER, QUEUE_WEATHER)
                .toF(RABBIT_URI, QUEUE_WEATHER, QUEUE_WEATHER + "2");

        fromF(RABBIT_URI, QUEUE_WEATHER, QUEUE_WEATHER)
                .log(DEBUG, "Read from Rabbit: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, WeatherDto.class)
                .process(this::enrichWeatherDto)
                .marshal().json(JsonLibrary.Jackson, WeatherDto.class)
                .log(DEBUG, "Starting http")
                .wireTap("rest:get:hello/W?host=localhost:8080&throwExceptionOnFailure=true&okStatusCodeRange=100-499&httpClientConfigurer=ipsRestHttpClientConfigurer")
                .to("rest:get:hello/N?host=localhost:8080&throwExceptionOnFailure=true&okStatusCodeRange=100-499&httpClientConfigurer=ipsRestHttpClientConfigurer")
                .log(DEBUG, "Done http ${body}")
        ;

        fromF(RABBIT_URI, QUEUE_WEATHER, QUEUE_WEATHER + "2")
                .log(DEBUG, "Read from Rabbit: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, WeatherDto.class)
                .process(this::enrichWeatherDto)
                .marshal().json(JsonLibrary.Jackson, WeatherDto.class)
                .log(DEBUG, "Starting http")
                .wireTap("rest:get:hello/2W?host=localhost:8080&throwExceptionOnFailure=true&okStatusCodeRange=100-499&httpClientConfigurer=ipsRestHttpClientConfigurer")
                .to("rest:get:hello/2N?host=localhost:8080&throwExceptionOnFailure=true&okStatusCodeRange=100-499&httpClientConfigurer=ipsRestHttpClientConfigurer")
                .log(DEBUG, "Done http ${body}")
        ;

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
