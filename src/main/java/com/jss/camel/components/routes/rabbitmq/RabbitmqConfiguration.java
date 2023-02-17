package com.jss.camel.components.routes.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.valueOf;

@Configuration
@ConditionalOnExpression("${jss.camel.rabbitmq.enabled:true} " +
        "|| ${jss.camel.rabbitmq-throttler.enabled:true} ")
public class RabbitmqConfiguration {

    public static String RMQ_HOST = "rmq.host";
    public static String RMQ_PORT = "rmq.port";

    @Bean
    public ConnectionFactory rabbitConnectionFactory2() {
        return factory();
    }

    public ConnectionFactory factory() {
        Properties properties = System.getProperties();
        String host = properties.getProperty(RMQ_HOST, "localhost");
        String port = properties.getProperty(RMQ_PORT, "5672");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(parseInt(port));
        factory.setUsername("guest");
        factory.setPassword("guest");
        return factory;
    }
}
