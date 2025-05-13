package com.jss.routes.rabbitmq;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ConditionalOnExpression(
        "${jss.camel.rabbitmq.enabled:true} "
                + "|| ${jss.camel.rabbitmq-throttler.enabled:true} "
                + "|| ${jss.camel.rabbitmq-stress-tester.enabled:true} ")
@Slf4j
public class RabbitmqConfiguration {

    public static final String QUEUE_WEATHER_EVENTS = "weather-events";
    public static final String WEATHER_COMMAND = "weather-command";

    public static String EXCHANGE_WEATHER_DATA = "weather.data";
    public static final String RABBIT_URI =
            "spring-rabbitmq:"
                    + EXCHANGE_WEATHER_DATA
                    + "?"
                    + "queues=%s&"
                    + "routingKey=%s&"
                    + "arg.queue.autoDelete=false&"
                    + "autoDeclare=true&"
                    + "concurrentConsumers=2&"
                    //+ "connectionFactory=#rabbitConnectionFactory&" +
                    + "args=#rabbitArgs";
    public static String QUEUE_WEATHER_DATA = "weather-data";
    public static String ROUTINGKEY_WEATHER_DATA = "weather-data";
    public static String RMQ_HOST = "rmq.host";
    public static String RMQ_PORT = "rmq.port";

    @Bean(name = "rabbitArgs")
    public Map<String, Object> rabbitArgs() {
        Map<String, Object> args = new HashMap<>();
        // args.put("x-message-ttl", 5000); // TTL of 5 seconds
        return args;
    }

    /**
     * camel-spring-rabbitmq lib will either create all channels under one connection or one channel
     * per connection using Scope=PROTOTYPE will create new connection per route
     *
     * @return
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ConnectionFactory rabbitConnectionFactory() {
        log.error("Setting Factory");
        Properties properties = System.getProperties();
        String host = properties.getProperty(RMQ_HOST, "localhost");
        String port = properties.getProperty(RMQ_PORT, "5672");

        return cachingFactory(host, port);
    }

    private ConnectionFactory cachingFactory(String host, String port) {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setAddresses(host + ":" + port);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        factory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED); // 175
        // factory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.SIMPLE); //167
        factory.setChannelCacheSize(1000);
        return factory;
    }
}
