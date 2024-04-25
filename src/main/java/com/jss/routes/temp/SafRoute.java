package com.jss.routes.temp;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SafRoute extends RouteBuilder {

    private static final String SEDA_ROUTE = "seda:saf";
    //    private static final String SEDA_ROUTE = "seda:saf?multipleConsumers=true&concurrentConsumers=1";
//    private static final String SEDA_ROUTE = "seda:saf?multipleConsumers=true&concurrentConsumers=1&queueFactory=#linkedBlockingQueueFactory";
//    private static final String SEDA_ROUTE = "direct:saf";
    private static final long TIMER_DELAY = 1000;

    private static final String CONSUMER_TYPE = "seda";

//    @Autowired
//    CamelContext context;

    ProducerTemplate template;

    @Override
    public void configure() throws Exception {

        getCamelContext().start();
        template = getCamelContext().createProducerTemplate();

        from(SEDA_ROUTE).toD(CONSUMER_TYPE + ":${header.dest}");

        from(CONSUMER_TYPE + ":saf-1").throttle(1).timePeriodMillis(TIMER_DELAY)
                .process(s -> printBody(s.getMessage().getBody()));

        from(CONSUMER_TYPE + ":saf-2").throttle(2).timePeriodMillis(TIMER_DELAY)
                .process(s -> printBody(s.getMessage().getBody()));

        from(CONSUMER_TYPE + ":saf-3").throttle(3).timePeriodMillis(TIMER_DELAY)
                .process(s -> printBody(s.getMessage().getBody()));

        testMocksAreValid();
    }

    private void printBody(Object body) {
        System.out.println("[" + new Date() + "] [Body: " + body + "]");
    }

    public void testMocksAreValid() throws InterruptedException {
        Map<String, List<String>> safMessage = new HashMap<>();
        safMessage.put("saf-3", List.of("saf-3.1", "saf-3.2", "saf-3.3", "saf-3.4", "saf-3.5", "saf-3.6"));
        safMessage.put("saf-2", List.of("saf-2.1", "saf-2.2", "saf-2.3", "saf-2.4"));
        safMessage.put("saf-1", List.of("saf-1.1", "saf-1.2"));

        System.out.println("JSS: " + template);
        for (int i = 0; i < 2; i++) {
            for (String key : safMessage.keySet()) {
                for (String value : safMessage.get(key)) {
                    String body = "[" + i + "] " + value;
                    template.sendBodyAndHeader(SEDA_ROUTE, body, "dest", key);
//                    template.sendBodyAndHeader(CONSUMER_TYPE + ":" + key, body, "dest", key);
                }
            }
        }
    }
}
