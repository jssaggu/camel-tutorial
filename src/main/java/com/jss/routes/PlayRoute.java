package com.jss.routes;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.camel.model.ClaimCheckOperation.GetAndRemove;
import static org.apache.camel.model.ClaimCheckOperation.Set;

import com.rabbitmq.client.ConnectionFactory;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ClaimCheckOperation;
import org.springframework.context.annotation.Bean;

// @Component
public class PlayRoute extends RouteBuilder {

    AtomicInteger counterSent = new AtomicInteger();
    AtomicInteger counterReceived = new AtomicInteger();

    @Override
    public void configure() throws Exception {
        // spotless:off
        from("direct:start")
                .to("mock:step-1")
                    .claimCheck(Set, "claim-tag-original")
                    .setBody(jsonpath("$.name"))
                .to("mock:step-2")
                    .claimCheck(Set, "claim-tag-step-2")
                .to("mock:step-3")
                    .claimCheck(GetAndRemove, "claim-tag-step-2")
                .to("mock:step-4")
                    .transform()
                    .constant("This message should be there in body even after claim 2.")
                    .claimCheck(ClaimCheckOperation.Get, "claim-tag-step-2")
                .to("mock:step-5")
                    .claimCheck(ClaimCheckOperation.Get, "claim-tag-original")
                .to("mock:step-6");
        // spotless:on

    }

    public void configure2() throws Exception {
        from("timer:time?period=100")
                .process(
                        exchange -> {
                            exchange.getIn().setBody(new Date());
                            counterSent.incrementAndGet();
                        })
                .to("seda:load");

        from("seda:load").to("direct:sleeper");

        from("direct:sleeper")
                .process(
                        e -> {
                            SECONDS.sleep(1);
                            System.out.print(
                                    "\rSent ["
                                            + counterSent
                                            + "] Received ["
                                            + counterReceived.incrementAndGet()
                                            + "]");
                        });
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
