package com.jss.camel.components.routes.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = {
        "jss.camel.rabbitmq.enabled"
},
        havingValue = "true")
public class RabbitmqConfiguration {

    @Bean
    public ConnectionFactory rabbitConnectionFactory2() {
        return factory();
    }

    public ConnectionFactory factory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        return factory;
    }
}
