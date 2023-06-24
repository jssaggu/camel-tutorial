package com.jss.routes;

import static com.jss.routes.WeatherRouteTestcontainersTest.DOCKER_RABBITMQ;
import static com.jss.routes.WeatherRouteTestcontainersTest.RABBITMQ_PORT;
import static com.jss.routes.WeatherRouteTestcontainersTest.SERVICE_NAME_RABBITMQ;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "jss.camel.testcontainers.enabled", havingValue = "true")
public class TestContainerLaunchConfig {

    @Bean
    public RabbitTemplate rabbitTemplate() {
        String host = DOCKER_RABBITMQ.getServiceHost(SERVICE_NAME_RABBITMQ, RABBITMQ_PORT);
        Integer port = DOCKER_RABBITMQ.getServicePort(SERVICE_NAME_RABBITMQ, RABBITMQ_PORT);
        RabbitConnectionFactoryBean factoryBean = new RabbitConnectionFactoryBean();
        factoryBean.setHost(host);
        factoryBean.setPort(port);
        factoryBean.setUsername("guest");
        factoryBean.setPassword("guest");
        factoryBean.setVirtualHost("/");

        factoryBean.afterPropertiesSet();

        final CachingConnectionFactory factory =
                new CachingConnectionFactory(factoryBean.getRabbitConnectionFactory());
        factory.afterPropertiesSet();
        return new RabbitTemplate(factory);
    }
}
