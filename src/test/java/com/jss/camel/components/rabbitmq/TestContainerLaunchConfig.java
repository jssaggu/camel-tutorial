package com.jss.camel.components.rabbitmq;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Properties;

import static com.jss.camel.components.rabbitmq.WeatherRouteTestcontainersTest.DOCKER_RABBITMQ;
import static com.jss.camel.components.routes.rabbitmq.RabbitmqConfiguration.RMQ_HOST;
import static com.jss.camel.components.routes.rabbitmq.RabbitmqConfiguration.RMQ_PORT;

@Configuration
@ConditionalOnProperty(name = "jss.camel.testcontainers.enabled", havingValue = "true")
public class TestContainerLaunchConfig {

    @PostConstruct
    public void constructed() {
        Properties props = System.getProperties();
        props.put(RMQ_HOST, "" + DOCKER_RABBITMQ.getHost());
        props.put(RMQ_PORT, "" + DOCKER_RABBITMQ.getMappedPort(5672));
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        String address = DOCKER_RABBITMQ.getHost();
        Integer port = DOCKER_RABBITMQ.getMappedPort(5672);
        RabbitConnectionFactoryBean factoryBean = new RabbitConnectionFactoryBean();
        factoryBean.setHost(address);
        factoryBean.setPort(port);
        factoryBean.setUsername("guest");
        factoryBean.setPassword("guest");
        factoryBean.setVirtualHost("/");

        factoryBean.afterPropertiesSet();

        final CachingConnectionFactory factory = new CachingConnectionFactory(
                factoryBean.getRabbitConnectionFactory());
        factory.afterPropertiesSet();
        return new RabbitTemplate(factory);
    }
}