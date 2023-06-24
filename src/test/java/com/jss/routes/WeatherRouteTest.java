package com.jss.routes;

import static com.jss.routes.rabbitmq.RabbitmqConfiguration.EXCHANGE_WEATHER_DATA;
import static com.jss.routes.rabbitmq.RabbitmqConfiguration.QUEUE_WEATHER_DATA;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jss.CamelApplication;
import com.jss.routes.rabbitmq.RabbitmqConfiguration;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled(
        "A RabbitMQ server must be running inorder to run this test."
                + "Once RabbitMQ is started. Remove @Disabled statement")
@SpringBootTest(
        classes = CamelApplication.class,
        properties = {"jss.camel.rabbitmq.enabled=true"})
@CamelSpringBootTest
class WeatherRouteTest {

    @Autowired private RabbitTemplate rabbitTemplate;

    @Test
    void sendAndReceiveMessage() {
        rabbitTemplate.send(EXCHANGE_WEATHER_DATA, QUEUE_WEATHER_DATA, message());

        Message response = rabbitTemplate.receive(RabbitmqConfiguration.QUEUE_WEATHER_EVENTS, 1000);
        assertNotNull(response, "Response must be non-null");

        String body = new String(response.getBody());

        assertTrue(body.contains("id"), "Id must be defined");
        assertTrue(body.contains("receivedTime"), "receivedTime must be defined");
    }

    private Message message() {
        return MessageBuilder.withBody(
                        "{ \"city\": \"London\", \"temp\": \"20\", \"unit\": \"C\"}".getBytes())
                .build();
    }
}
