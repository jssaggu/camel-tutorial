package com.jss.camel.components.routes.errorhandlers;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.camel.LoggingLevel.ERROR;
import static org.apache.camel.LoggingLevel.INFO;
import static org.apache.camel.LoggingLevel.WARN;

@Component
@ConditionalOnProperty(name = "jss.camel.error-handlers2.enabled", havingValue = "true")
public class OnExceptionRoute extends RouteBuilder {

    final static AtomicInteger counter = new AtomicInteger(1);

    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .log(ERROR, "JSS: ${exception}")
                .handled(true)
                .to("direct:exceptionHandler")
        ;

        from("timer:time?period=1000")
                .process(exchange -> exchange.getIn().setBody(new Date()))
                .choice()
                    .when(e -> counter.incrementAndGet() % 2 == 0)
                        .bean(HelloBean.class, "callBad")
                    .otherwise()
                        .bean(HelloBean.class, "callGood")
                    .end()
                .log(INFO, ">> ${header.firedTime} >> ${body}")
                .to("log:reply");
    }
}
