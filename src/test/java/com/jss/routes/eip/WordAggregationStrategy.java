package com.jss.routes.eip;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

public class WordAggregationStrategy implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }
        String body = newExchange.getIn().getBody(String.class).trim();
        String existing = oldExchange.getIn().getBody(String.class).trim();
        oldExchange.getIn().setBody(existing + " + " + body);
        return oldExchange;
    }
}
