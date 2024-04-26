package com.jss.routes;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AggregationStrategies;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.processor.aggregate.UseOriginalAggregationStrategy;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

@MockEndpoints()
public class SafTest extends CamelTestSupport {

    private static final String SEDA_ROUTE = "seda:saf";
    //    private static final String SEDA_ROUTE = "seda:saf?multipleConsumers=false&concurrentConsumers=10";
    private static final long TIMER_DELAY = 2000;
    //    private static final String CONSUMER_TYPE = "direct";
    private static final String CONSUMER_TYPE = "seda";
    //    private static final String CONSUMER_SUFFIX = "?multipleConsumers=false&concurrentConsumers=10";
    private static final String CONSUMER_SUFFIX = "";
    private static final String CONNECTION_1 = "CON_1";
    private static final String CONNECTION_2 = "CON_2";
    private static final String CONNECTION_3 = "CON_3";

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
//                from(SEDA_ROUTE).toD(CONSUMER_TYPE + ":${header.dest}" + CONSUMER_SUFFIX);

                from(CONSUMER_TYPE + ":" + CONNECTION_1 + CONSUMER_SUFFIX).throttle(1).timePeriodMillis(TIMER_DELAY)
                        .process(s -> printBody(s));

                from(CONSUMER_TYPE + ":" + CONNECTION_2 + CONSUMER_SUFFIX).throttle(2).timePeriodMillis(TIMER_DELAY)
                        .process(s -> printBody(s));

                from(CONSUMER_TYPE + ":" + CONNECTION_3 + CONSUMER_SUFFIX).throttle(3).timePeriodMillis(TIMER_DELAY)
                        .process(s -> printBody(s));
            }
        };
    }

    private void printBody(Exchange exchange) {
        System.out.println("" +
                "[" + new Date() + "]" +
                "[" + exchange.getMessage().getHeader("dest") + "]" +
                "[Body: " + exchange.getMessage().getBody() + "] "
        );
    }

    @Test
    public void testMocksAreValid() throws InterruptedException {

        Map<String, List<String>> safMessage = new HashMap<>();
        safMessage.put(CONNECTION_3, List.of("saf-3.1", "saf-3.2", "saf-3.3", "saf-3.4", "saf-3.5", "saf-3.6"));
        safMessage.put(CONNECTION_2, List.of("saf-2.1", "saf-2.2", "saf-2.3", "saf-2.4"));
        safMessage.put(CONNECTION_1, List.of("saf-1.1", "saf-1.2"));

        for (int i = 0; i < 20; i++) { //Repeats
            for (String routeName : safMessage.keySet()) { //Find route id
                for (String message : safMessage.get(routeName)) { //Send map message to above roue id i.e.
                    String body = "[" + i + "] " + message;
//                    template.sendBodyAndHeader(SEDA_ROUTE, body, "dest", routeName);
                    template.sendBodyAndHeader(CONSUMER_TYPE + ":" + routeName + CONSUMER_SUFFIX, body, "dest", routeName);
                }
            }
        }

        SECONDS.sleep(20);
    }
}
