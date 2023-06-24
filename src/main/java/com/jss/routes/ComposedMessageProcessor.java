package com.jss.routes;

import static java.util.Objects.nonNull;
import static org.apache.camel.LoggingLevel.ERROR;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

// @Component
public class ComposedMessageProcessor extends RouteBuilder {
    static int i = 0;

    @Override
    public void configure() throws Exception {
        from("direct:startCMP")
                .split(body().tokenize("@"), new MyOrderStrategy())
                .bean(new MyOrderService(), "handleOrder")
                .log(ERROR, "${body}")
                .end()
                .bean(new MyOrderService(), "buildCombinedResponse");
    }

    static class MyOrderService {
        public void handleOrder(Exchange e) {
            System.out.println("MyOrderService 1: " + e.getMessage().getBody());
        }

        public void buildCombinedResponse(Exchange e) {
            System.out.println("MyOrderService 2: " + e.getMessage().getBody());
        }
    }

    private class MyOrderStrategy implements AggregationStrategy {
        @Override
        public Exchange aggregate(Exchange oldE, Exchange newE) {
            i++;
            if (nonNull(oldE)) {
                String msg =
                        i
                                + ".(Old1["
                                + (oldE != null ? oldE.getMessage().getBody() : "")
                                + "]"
                                + i
                                + ".New1["
                                + newE.getMessage().getBody()
                                + "]).";
                oldE.getMessage().setBody(msg + "\n");
                return oldE;
            } else {
                String msg = i + ".(New2[" + newE.getMessage().getBody() + "]).";
                newE.getMessage().setBody(msg + "\n");
                return newE;
            }
        }
    }
}
