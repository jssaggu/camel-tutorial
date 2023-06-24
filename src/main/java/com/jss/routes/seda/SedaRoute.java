package com.jss.routes.seda;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.DefaultMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * .to("seda:complexProcess");
 *
 * <p>from("seda:complexProcess?multipleConsumers=true") .to("direct:complexProcess");
 */
@Component
@ConditionalOnProperty(name = "jss.camel.seda.enabled", havingValue = "true")
public class SedaRoute extends RouteBuilder {

    static AtomicInteger counter = new AtomicInteger();

    @Override
    public void configure() {
        from("timer:ping?period=100")
                .routeId("Timer")
                .process(
                        exchange -> {
                            counter.incrementAndGet();
                            Message message = new DefaultMessage(exchange);
                            message.setBody(new Date());
                            exchange.setMessage(message);
                        })
                .to("seda:weightLifter?multipleConsumers=true");

        from("seda:weightLifter?multipleConsumers=true")
                .routeId("Seda-WeightLifter")
                .to("direct:complexProcess");

        from("direct:complexProcess")
                .routeId("Direct-ComplexProcess")
                //                .log(ERROR, "${body}")
                .process(
                        exchange -> {
                            if (new Random().nextInt(20) < 5) {
                                throw new Exception();
                            }
                        })
                .process(
                        exchange -> {
                            SECONDS.sleep(5);
                            int total = counter.decrementAndGet();
                            System.out.println("Counter: " + total);
                        })
                .end();
    }

    public static void main(String[] args) {
        int counter = 200;

        System.out.println("> " + counter % 100);
    }
}
