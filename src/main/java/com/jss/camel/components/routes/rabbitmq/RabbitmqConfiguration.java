package com.jss.camel.components.routes.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

import static java.lang.Integer.parseInt;

@Configuration
@ConditionalOnExpression("${jss.camel.rabbitmq.enabled:true} " +
        "|| ${jss.camel.rabbitmq-throttler.enabled:true} ")
public class RabbitmqConfiguration {
    public static String EXCHANGE_WEATHER_DATA = "weather.data";
    public static String QUEUE_WEATHER_DATA = "weather-data";
    public static String ROUTINGKEY_WEATHER_DATA = "weather-data";

    public static final String RABBIT_URI =
            "spring-rabbitmq:" + EXCHANGE_WEATHER_DATA + "?queues=%s&routingKey=%s&arg.queue.autoDelete=false&autoDeclare=true";
    public static final String QUEUE_WEATHER = "weather";
    public static final String QUEUE_WEATHER_EVENTS = "weather-events";

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
        factory.setHost(host);
        factory.setPort(parseInt(port));
        factory.setUsername("guest");
        factory.setPassword("guest");
        return factory;
    }

    @Bean
    public Queue weatherData() {
        return new Queue(QUEUE_WEATHER_DATA);
    }

    @Bean
    public Exchange weatherDirectExchange() {
        return new DirectExchange(EXCHANGE_WEATHER_DATA);
    }

    @Bean
    public Binding weatherDataBinding() {
        return BindingBuilder
                .bind(weatherData())
                .to(weatherDirectExchange())
                .with(ROUTINGKEY_WEATHER_DATA)
                .noargs();
    }

}
