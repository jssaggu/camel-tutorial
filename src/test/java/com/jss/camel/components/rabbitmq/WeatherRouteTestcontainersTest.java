package com.jss.camel.components.rabbitmq;

import com.jss.CamelApplication;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static com.jss.camel.components.routes.rabbitmq.WeatherRoute.EXCHANGE_WEATHER;
import static com.jss.camel.components.routes.rabbitmq.WeatherRoute.QUEUE_WEATHER;
import static com.jss.camel.components.routes.rabbitmq.WeatherRoute.QUEUE_WEATHER_EVENTS;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        classes = CamelApplication.class,
        properties = {
                "jss.camel.rabbitmq.enabled=true",
                "jss.camel.testcontainers.enabled=true"
        })
@CamelSpringBootTest
@Testcontainers
@ContextConfiguration(classes = {TestContainerLaunchConfig.class}, loader = AnnotationConfigContextLoader.class)
class WeatherRouteTestcontainersTest {

    private static final String RABBIT_UP_LOG_MESSAGE = ".*Resetting node maintenance status.*";

    @Container
    public static GenericContainer DOCKER_RABBITMQ = new GenericContainer(
            DockerImageName.parse("rabbitmq:3-management"))
            .withExposedPorts(5672)
            .waitingFor(Wait.forLogMessage(RABBIT_UP_LOG_MESSAGE, 1)
                    .withStartupTimeout(Duration.ofSeconds(20)));

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void sendAndReceiveMessage() {
        rabbitTemplate.send(EXCHANGE_WEATHER, QUEUE_WEATHER, message());

        Message response = rabbitTemplate.receive(QUEUE_WEATHER_EVENTS, 1000);
        assertNotNull(response, "Response must be non-null");

        String body = new String(response.getBody());

        assertTrue(body.contains("id"), "Id must be defined");
        assertTrue(body.contains("receivedTime"), "receivedTime must be defined");
    }

    private Message message() {
        return MessageBuilder
                .withBody("{ \"city\": \"London\", \"temp\": \"20\", \"unit\": \"C\"}"
                        .getBytes())
                .build();
    }
}