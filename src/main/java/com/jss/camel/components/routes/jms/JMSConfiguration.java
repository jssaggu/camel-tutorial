package com.jss.camel.components.routes.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.ConnectionFactory;

@Configuration
@ConditionalOnProperty(name = "jss.camel.jms.enabled", havingValue = "true")
public class JMSConfiguration {

    @Bean
    public ConnectionFactory connectionFactory() {
        /**
         * ConnectionFactory factory = new RMQConnectionFactory();
         * ConnectionFactory factory = new ActiveMQConnectionFactory();
         * getContext().addComponent("jms", jmsComponentAutoAcknowledge(factory));
         */
        return new ActiveMQConnectionFactory("tcp://localhost:61616");
    }
}
