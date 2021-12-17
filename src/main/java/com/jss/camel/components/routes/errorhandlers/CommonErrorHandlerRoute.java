package com.jss.camel.components.routes.errorhandlers;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.camel.LoggingLevel.WARN;

@Component
@ConditionalOnProperty(name = "jss.camel.error-handlers.enabled2", havingValue = "true")
public class CommonErrorHandlerRoute extends RouteBuilder {

    public final static AtomicInteger COUNTER = new AtomicInteger(1);

    @Override
    public void configure() throws Exception {
        from("direct:exceptionHandler")
                .log(WARN, "In Exception Handler")
//                .process(e -> SECONDS.sleep(5))
                .log(WARN, "${body}");
    }
}
