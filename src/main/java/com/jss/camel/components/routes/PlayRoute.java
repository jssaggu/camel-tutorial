package com.jss.camel.components.routes;

import com.rabbitmq.client.ConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

//@Component
public class PlayRoute extends RouteBuilder {

    AtomicInteger counterSent = new AtomicInteger();
    AtomicInteger counterReceived = new AtomicInteger();

    @Override
    public void configure() throws Exception {
        from("timer:time?period=100")
                .process(exchange ->
                {
                    exchange.getIn().setBody(new Date());
                    counterSent.incrementAndGet();
                })
                .to("seda:load");

        from("seda:load")
                .to("direct:sleeper");

        from("direct:sleeper")

                .process(e -> {
                    SECONDS.sleep(1);
                    System.out.print("\rSent [" + counterSent + "] Received [" + counterReceived.incrementAndGet() + "]");
                })
        ;
    }

    @Bean("jssSagguBean")
    public ConnectionFactory jsSagguBean() {
//        return new JSSBean("Using jsSagguBean");
        return new ConnectionFactory();
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
}
