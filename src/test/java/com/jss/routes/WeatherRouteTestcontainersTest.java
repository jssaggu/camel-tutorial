package com.jss.routes;

import static com.jss.routes.rabbitmq.RabbitmqConfiguration.EXCHANGE_WEATHER_DATA;
import static com.jss.routes.rabbitmq.RabbitmqConfiguration.QUEUE_WEATHER_DATA;
import static com.jss.routes.rabbitmq.RabbitmqConfiguration.QUEUE_WEATHER_EVENTS;
import static com.jss.routes.rabbitmq.RabbitmqConfiguration.RMQ_HOST;
import static com.jss.routes.rabbitmq.RabbitmqConfiguration.RMQ_PORT;
import static com.rabbitmq.client.BuiltinExchangeType.DIRECT;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jss.CamelApplication;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.File;
import java.time.Duration;
import java.util.Properties;
import java.util.function.Function;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
        classes = CamelApplication.class,
        properties = {"jss.camel.rabbitmq.enabled=true", "jss.camel.testcontainers.enabled=true"})
@CamelSpringBootTest
@Testcontainers
@ContextConfiguration(
        classes = {TestContainerLaunchConfig.class},
        loader = AnnotationConfigContextLoader.class)
@Slf4j
/**
 * This integration test performs the following steps to test a route:<br>
 * 1. Launch RabbitMQ docker container<br>
 * 2. Declare Weather exchange and event queues, and bind them<br>
 * 3. Send a message to weather-data queue using RabbitmqTemplate<br>
 * 4. Camel route will consume the message and send it to weather-event routing-key<br>
 * 5. Consume the message using RabbitmqTemplate<br>
 * 6. Verify message has been processed and enriched by the Camel route<br>
 */
class WeatherRouteTestcontainersTest {

    public static final String SERVICE_NAME_RABBITMQ = "rabbitmq";
    public static final int RABBITMQ_PORT = 5672;
    private static final String RABBIT_UP_LOG_MESSAGE = ".*Resetting node maintenance status.*";

    static Function<String, WaitStrategy> waitForLogMessageFunction =
            (String messageInLog) ->
                    Wait.forLogMessage(messageInLog, 1).withStartupTimeout(Duration.ofSeconds(180));

    /*  @Container
        public static GenericContainer DOCKER_RABBITMQ = new GenericContainer(
                DockerImageName.parse("rabbitmq:3-management"))
                .withExposedPorts(RABBITMQ_PORT)
                .waitingFor(Wait.forLogMessage(RABBIT_UP_LOG_MESSAGE, 1)
                        .withStartupTimeout(Duration.ofSeconds(20)));
    */
    @Container
    public static DockerComposeContainer DOCKER_RABBITMQ =
            new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
                    .withExposedService(SERVICE_NAME_RABBITMQ, RABBITMQ_PORT)
                    .waitingFor(
                            SERVICE_NAME_RABBITMQ,
                            waitForLogMessageFunction.apply(RABBIT_UP_LOG_MESSAGE));

    @Autowired private RabbitTemplate rabbitTemplate;

    @SneakyThrows
    @BeforeAll
    public static void constructed() {
        DOCKER_RABBITMQ.start();
        Properties props = System.getProperties();
        props.put(
                RMQ_HOST,
                "" + DOCKER_RABBITMQ.getServiceHost(SERVICE_NAME_RABBITMQ, RABBITMQ_PORT));
        props.put(
                RMQ_PORT,
                "" + DOCKER_RABBITMQ.getServicePort(SERVICE_NAME_RABBITMQ, RABBITMQ_PORT));

        // Below section is added to support declaring of queues manually as new
        // camel-spring-rabbitmq doesn't
        // create them
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(RABBITMQ_PORT);
        factory.setUsername("guest");
        factory.setPassword("guest");

        // Create a new connection and channel
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            // Declare a new exchange
            channel.exchangeDeclare(EXCHANGE_WEATHER_DATA, DIRECT, true);

            // Declare a new queue
            channel.queueDeclare(QUEUE_WEATHER_EVENTS, true, false, false, null);

            // Assert that the queue exists
            AMQP.Queue.DeclareOk result = channel.queueDeclarePassive(QUEUE_WEATHER_EVENTS);
            Assertions.assertNotNull(result);

            // Bind exchange, routingKey and queue
            AMQP.Queue.BindOk bindOk =
                    channel.queueBind(
                            QUEUE_WEATHER_EVENTS, EXCHANGE_WEATHER_DATA, QUEUE_WEATHER_EVENTS);
            log.info("Weather Event Binding Result: {}", bindOk);
        }
    }

    @AfterAll
    public static void afterAll() {
        DOCKER_RABBITMQ.stop();
    }

    @SneakyThrows
    @Test
    void sendAndReceiveMessage() {
        rabbitTemplate.send(EXCHANGE_WEATHER_DATA, QUEUE_WEATHER_DATA, message());

        Message response = rabbitTemplate.receive(QUEUE_WEATHER_EVENTS, 1000);
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
