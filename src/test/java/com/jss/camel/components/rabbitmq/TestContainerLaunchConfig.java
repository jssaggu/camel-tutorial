package com.jss.camel.components.rabbitmq;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Properties;

import static com.jss.camel.components.rabbitmq.WeatherRouteTest.rabbitmq;
import static com.jss.camel.components.routes.rabbitmq.RabbitmqConfiguration.RMQ_HOST;
import static com.jss.camel.components.routes.rabbitmq.RabbitmqConfiguration.RMQ_PORT;

@Configuration
public class TestContainerLaunchConfig implements
        ApplicationContextInitializer<ConfigurableApplicationContext> {

    @PostConstruct
    public void constructed() {
        Properties props = System.getProperties();
        props.put(RMQ_HOST, "" + rabbitmq.getHost());
        props.put(RMQ_PORT, "" + rabbitmq.getMappedPort(5672));
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        String address = rabbitmq.getHost();
        Integer port = rabbitmq.getMappedPort(5672);

        System.setProperty("spring.rabbitmq.host", address);
        System.setProperty("spring.rabbitmq.port", port + "");
    }
}