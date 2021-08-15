package com.jss.camel.components.routes;

import com.jss.camel.components.routes.temp.JSSBean;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static org.apache.camel.LoggingLevel.ERROR;

/**
 *
 *
 from("direct:greeting")
 .log(ERROR, "Hello ${body}")
 .process(exchange -> {
 String msg = exchange.getMessage().getBody(String.class);
 exchange.getMessage().setBody(msg + ". Good to see you.");
 })
 .log(ERROR, "${body}")
 .choice()
 .when().simple("${body} contains 'Team'")
 .log(ERROR, "I like working with Teams")
 .otherwise()
 .log(ERROR, "Solo fighter :)")
 .end()
 .end();
 */
@Component
@ConditionalOnProperty(name = "jss.camel.hello.enabled", havingValue = "true")
public class HelloRoute extends RouteBuilder {

    private final ConnectionFactory jssBean;

    public HelloRoute(ConnectionFactory jssSagguBean) {
        this.jssBean = jssSagguBean;
    }

    @Override
    public void configure() {

        System.out.println("JSS >> " + jssBean);

        from("direct:greeting")
                .log(ERROR, "Hello ${body}")
                .choice()
                    .when().simple("${body} contains 'Team'")
                        .log(ERROR, "I like working with Teams")
                .otherwise()
                        .log(ERROR, "Solo fighter :)")
                .end()
                .end();
    }
}