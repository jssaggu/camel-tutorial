package com.jss.routes.aggregation;

import java.util.Date;
import java.util.Random;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jss.camel.aggregator.enabled", havingValue = "true")
public class AggregatorRoute extends RouteBuilder {
    final String CORRELATION_ID = "correlationId";

    @Override
    public void configure() throws Exception {

        Random random = new Random();

        from("timer:insurance?period=200")
                .process(
                        exchange -> {
                            Message message = exchange.getMessage();
                            message.setHeader(CORRELATION_ID, random.nextInt(4));
                            message.setBody(new Date() + "");
                        })
                .aggregate(header(CORRELATION_ID), new MyAggregationStrategy())
                .completionSize(5)
                .log(LoggingLevel.ERROR, "${header." + CORRELATION_ID + "} ${body}");
    }
}
