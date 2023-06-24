package com.jss.routes.errorhandlers;

import static org.apache.camel.LoggingLevel.WARN;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jss.camel.error-handlers.enabled2", havingValue = "true")
public class CommonErrorHandlerRoute extends RouteBuilder {

    public static final AtomicInteger COUNTER = new AtomicInteger(1);

    @Override
    public void configure() throws Exception {
        from("direct:exceptionHandler")
                .log(WARN, "In Exception Handler")
                //                .process(e -> SECONDS.sleep(5))
                .log(WARN, "${body}");
    }
}
