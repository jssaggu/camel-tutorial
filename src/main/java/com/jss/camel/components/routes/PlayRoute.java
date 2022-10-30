package com.jss.camel.components.routes;

import com.jss.camel.components.routes.temp.JSSBean;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.camel.LoggingLevel.ERROR;

//@Component
public class PlayRoute extends RouteBuilder {

    AtomicInteger counter = new AtomicInteger();

    @Override
    public void configure() throws Exception {
        from("timer:time?period=100")
                .threads(2, 50)
                .process(exchange -> exchange.getIn().setBody(new Date()))
//                .process(exchange -> exchange.getIn().getHeaders())
//                .bean(HelloBean.class, "get2")
//                .log(ERROR, ">> ${header.firedTime} >> ${body}")
                .to("direct:sleeper");

        from("direct:sleeper")

                .process(e -> {
                    int c = counter.incrementAndGet();
                    System.out.println("[" + c + "] Sleeping...");
                    SECONDS.sleep(10);
                    System.out.println("[" + c + "] Up...");
                })
        ;
    }

    private static class HelloBean {
        public HelloBean() {
            System.out.println("HellBean Constructor");
        }

        public String get() {
            System.out.println("Hello Bean1..." + new Date());
            return "HelloBean1::" + new Date();
        }

        public String get2(String firedTime) {
            System.out.println("Hello Bean2..." + firedTime);
            return "HelloBean2::" + new Date();
        }
    }

    @Bean("jssSagguBean")
    public ConnectionFactory jsSagguBean() {
//        return new JSSBean("Using jsSagguBean");
        return new ConnectionFactory();
    }
}
