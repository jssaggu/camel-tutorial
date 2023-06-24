package com.jss.routes;

import static org.apache.camel.LoggingLevel.ERROR;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * from("direct:greeting") .log(ERROR, "Hello ${body}") .process(exchange -> { String msg =
 * exchange.getMessage().getBody(String.class); exchange.getMessage().setBody(msg + ". Good to see
 * you."); }) .log(ERROR, "${body}") .choice() .when().simple("${body} contains 'Team'") .log(ERROR,
 * "I like working with Teams") .otherwise() .log(ERROR, "Solo fighter :)") .end() .end();
 */
@Component
@ConditionalOnProperty(name = "jss.camel.hello.enabled", havingValue = "true")
public class HelloRoute extends RouteBuilder {

    @Override
    public void configure() {

        from("direct:greeting")
                .id("greeting")
                .log(ERROR, "Hello ${body}")
                .choice()
                .when()
                .simple("${body} contains 'Team'")
                .log(ERROR, "I like working with Teams")
                .otherwise()
                .log(ERROR, "Solo fighter :)")
                .end()
                .to("direct:finishGreeting");

        from("direct:finishGreeting").log(ERROR, "Bye ${body}");
    }
}
