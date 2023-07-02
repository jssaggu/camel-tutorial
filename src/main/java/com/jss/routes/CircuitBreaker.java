package com.jss.routes;

import static org.apache.camel.LoggingLevel.INFO;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jss.camel.circuit-breaker.enabled", havingValue = "true")
public class CircuitBreaker extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        // spotless:off
        from("timer:time?period=1000")
                .circuitBreaker()
                .resilience4jConfiguration()
                    .timeoutEnabled(true)
                    .timeoutDuration(500)
                    .minimumNumberOfCalls(2)
                    .waitDurationInOpenState(5)
                    .failureRateThreshold(99)
                    .automaticTransitionFromOpenToHalfOpenEnabled(true)
                    .permittedNumberOfCallsInHalfOpenState(1)
                .end()
                .log(INFO, "------------Start----------------------------")
                .to("http://localhost:8080/hello?sleepTimeMills=1000")
                .onFallback()
                .transform()
                .constant("Static Fallback Message")
                .end()
                .log(INFO, "Body - ${body}")
                .log(INFO, "------------End----------------------------");
        // spotless:on
    }
}
