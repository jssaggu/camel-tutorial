package com.jss.config;

import com.rabbitmq.client.ConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.micrometer.messagehistory.MicrometerMessageHistoryFactory;
import org.apache.camel.component.micrometer.routepolicy.MicrometerRoutePolicyFactory;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelConfiguration {

    private static String EX_DIRECT = "jss.direct";
    private static String EX_TOPIC = "jss.topic";
    public static final String RABBIT_URI_NO_ROUTING_KEY = "rabbitmq:" + EX_DIRECT + "?queue=%s&autoDelete=false";
    public static final String RABBIT_URI = "rabbitmq:" + EX_DIRECT + "?queue=%s&routingKey=%s&autoDelete=false";
    public static final String RABBIT_URI_TOPIC = "rabbitmq:" + EX_TOPIC + "?exchangeType=topic&queue=%s&routingKey=%s&autoDelete=false";
    public static final String RABBIT_URI_TOPIC_NO_ROUTING_KEY = "rabbitmq:" + EX_TOPIC + "?exchangeType=topic&queue=%s&autoDelete=false";

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

    @Bean
    public CamelContextConfiguration camelContextConfiguration() {

        return new CamelContextConfiguration() {
            @Override
            public void beforeApplicationStart(CamelContext camelContext) {
                camelContext.addRoutePolicyFactory(new MicrometerRoutePolicyFactory());
                camelContext.setMessageHistoryFactory(new MicrometerMessageHistoryFactory());
            }

            @Override
            public void afterApplicationStart(CamelContext camelContext) {

            }
        };
    }
}
