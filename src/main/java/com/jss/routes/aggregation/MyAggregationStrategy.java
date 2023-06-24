package com.jss.routes.aggregation;

import java.util.Objects;
import org.apache.camel.Exchange;

public class MyAggregationStrategy implements org.apache.camel.AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (Objects.isNull(oldExchange)) {
            return newExchange;
        }

        String oldBody = oldExchange.getIn().getBody(String.class);
        String newBody = newExchange.getIn().getBody(String.class);

        String aggBody = oldBody + "->" + newBody;

        oldExchange.getIn().setBody(aggBody);

        return oldExchange;
    }
}
