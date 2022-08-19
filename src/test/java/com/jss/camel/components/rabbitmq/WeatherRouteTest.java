package com.jss.camel.components.rabbitmq;

import com.jss.CamelApplication;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.jss.camel.components.routes.rabbitmq.WeatherRoute.EXCHANGE_WEATHER;
import static com.jss.camel.components.routes.rabbitmq.WeatherRoute.QUEUE_WEATHER;
import static com.jss.camel.components.routes.rabbitmq.WeatherRoute.QUEUE_WEATHER_EVENTS;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled(
        "A RabbitMQ server must be running inorder to run this test." +
                "Once RabbitMQ is started. Remove @Disabled statement"
)
@SpringBootTest(
        classes = CamelApplication.class,
        properties = {"jss.camel.rabbitmq.enabled=true"}
)
@CamelSpringBootTest
class WeatherRouteTest {

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