package com.jss.routes;

import org.apache.camel.AggregationStrategy;
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
    private static final String CONSUMER_TYPE = "seda";
//    private static final String CONSUMER_SUFFIX = "?multipleConsumers=false&concurrentConsumers=10";
    private static final String CONSUMER_SUFFIX = "";

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(SEDA_ROUTE).toD(CONSUMER_TYPE + ":${header.dest}" + CONSUMER_SUFFIX);

                from(CONSUMER_TYPE + ":saf-1" + CONSUMER_SUFFIX).throttle(1).timePeriodMillis(TIMER_DELAY)
//                        .aggregate(constant(true), new UseOriginalAggregationStrategy()).completionSize(10)
                        .process(s -> printBody(s.getMessage().getBody()));

                from(CONSUMER_TYPE + ":saf-2" + CONSUMER_SUFFIX).throttle(2).timePeriodMillis(TIMER_DELAY)
                        .process(s -> printBody(s.getMessage().getBody()));

                from(CONSUMER_TYPE + ":saf-3" + CONSUMER_SUFFIX).throttle(3).timePeriodMillis(TIMER_DELAY)
                        .process(s -> printBody(s.getMessage().getBody()));
            }
        };
    }

    private void printBody(Object body) {
        System.out.println("[" + new Date() + "] [Body: " + body + "]");
    }

    @Test
    public void testMocksAreValid() throws InterruptedException {

        Map<String, List<String>> safMessage = new HashMap<>();
        safMessage.put("saf-3", List.of("saf-3.1", "saf-3.2", "saf-3.3", "saf-3.4", "saf-3.5", "saf-3.6"));
        safMessage.put("saf-2", List.of("saf-2.1", "saf-2.2", "saf-2.3", "saf-2.4"));
        safMessage.put("saf-1", List.of("saf-1.1", "saf-1.2"));

        for (int i = 0; i < 20; i++) {
            for (String key : safMessage.keySet()) {
                for (String value : safMessage.get(key)) {
                    String body = "[" + i + "] " + value;
//                    template.sendBodyAndHeader(SEDA_ROUTE, body, "dest", key);
                    template.sendBodyAndHeader(CONSUMER_TYPE + ":" + key + CONSUMER_SUFFIX, body, "dest", key);
                }
            }
        }

        Thread.sleep(100000);
    }
}
