package com.jss.camel.components.routes.errorhandlers;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jss.camel.components.routes.errorhandlers.CommonErrorHandlerRoute.COUNTER;
import static org.apache.camel.LoggingLevel.INFO;

@Component
@ConditionalOnProperty(name = "jss.camel.error-handlers.enabled2", havingValue = "true")
public class ErrorHandlerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        errorHandler(
                deadLetterChannel("direct:exceptionHandler")
                .maximumRedeliveries(2)
        );

        from("timer:time?period=1000")
                .process(exchange -> exchange.getIn().setBody(new Date()))
                .choice()
                   .when(e -> COUNTER.incrementAndGet() % 2 == 0)
                        .bean(HelloBean.class, "callBad")
                    .otherwise()
                        .bean(HelloBean.class, "callGood")
                    .end()
                .log(INFO, ">> ${header.firedTime} >> ${body}")
                .to("log:reply");
    }
}
