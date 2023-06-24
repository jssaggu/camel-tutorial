package com.jss.routes.errorhandlers;

import static org.apache.camel.LoggingLevel.INFO;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jss.camel.error-handlers.enabled2", havingValue = "true")
public class TryCatchRoute extends RouteBuilder {

    static final AtomicInteger counter = new AtomicInteger(1);

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
