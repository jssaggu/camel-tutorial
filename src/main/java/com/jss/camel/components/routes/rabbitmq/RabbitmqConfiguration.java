package com.jss.camel.components.routes.rabbitmq;

import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnExpression(
        "${jss.camel.rabbitmq.enabled:true} " + "|| ${jss.camel.rabbitmq-throttler.enabled:true} ")
@Slf4j
public class RabbitmqConfiguration {
    public static final String QUEUE_WEATHER_EVENTS = "weather-events";
    public static String EXCHANGE_WEATHER_DATA = "weather.data";
    public static final String RABBIT_URI =
            "spring-rabbitmq:"
                    + EXCHANGE_WEATHER_DATA
                    + "?"
                    + "queues=%s&"
                    + "routingKey=%s&"
                    + "arg.queue.autoDelete=false&"
                    + "autoDeclare=true";
    public static String QUEUE_WEATHER_DATA = "weather-data";
    public static String ROUTINGKEY_WEATHER_DATA = "weather-data";
    public static String RMQ_HOST = "rmq.host";
    public static String RMQ_PORT = "rmq.port";

    @Bean
    public CachingConnectionFactory rabbitConnectionFactory2() {
        return factory();
    }

    public CachingConnectionFactory factory() {
        Properties properties = System.getProperties();
        String host = properties.getProperty(RMQ_HOST, "localhost");
        String port = properties.getProperty(RMQ_PORT, "5672");
        CachingConnectionFactory factory = new CachingConnectionFactory();
        //        factory.setAddresses("localhost:5671,localhost:5672");
        factory.setAddresses(host + ":" + port);
        factory.setUsername("guest");
        factory.setPassword("guest");
        return factory;
    }
}
