package com.jss.camel.components.routes.errorhandlers;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.camel.LoggingLevel.INFO;
import static org.apache.camel.LoggingLevel.WARN;

@Component
@ConditionalOnProperty(name = "jss.camel.error-handlers.enabled2", havingValue = "true")
public class TryCatchRoute extends RouteBuilder {

    final static AtomicInteger counter = new AtomicInteger(1);

    @Override
    public void configure() throws Exception {
         from("timer:time?period=1000")
                .process(exchange -> exchange.getIn().setBody(new Date()))
                    .doTry()
                        .bean(HelloBean.class, "callBad")
                    .doCatch(Exception.class)
                        .to("direct:exceptionHandler")
                    .end()
                .log(INFO, ">> ${header.firedTime} >> ${body}")
                .to("log:reply");
    }
}
