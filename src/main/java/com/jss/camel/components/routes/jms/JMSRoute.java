package com.jss.camel.components.routes.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;

import static org.apache.camel.component.jms.JmsComponent.jmsComponentAutoAcknowledge;

//@Component
public class JMSRoute extends RouteBuilder {

    //    private final CamelConfiguration camelConfiguration;

    @Override
    public void configure() throws Exception {
        //ConnectionFactory connectionFactory = new RMQConnectionFactory();
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        getContext().addComponent("jms",
                jmsComponentAutoAcknowledge(connectionFactory));
        from("jms:my-queue:my-orders")
                .log(LoggingLevel.ERROR, "${body}")
                .choice()
                .when(e -> e.getMessage().getBody().toString().contains("card"))
                .wireTap("jms:fraud-check:messages")
                .end()
        ;
    }
}
