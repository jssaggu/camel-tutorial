package com.jss.routes.eip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

public class SplitterRouteTest extends CamelTestSupport {

    @Test
    void splitEip() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:split");
        mock.expectedBodiesReceived("A", "B", "C");
        //        List<String> body = Arrays.asList("A", "B", "C");
        template.sendBody("direct:start", "A#B#C");

        mock.assertIsSatisfied();
    }

    @Test
    void complexSplitEip() throws Exception {
        // Order
        List<Order> orders = new ArrayList<>();
        orders.add(
                Order.builder()
                        .orderId("O100")
                        .itemIds(Arrays.asList("I100", "I101", "I102"))
                        .build());
        orders.add(
                Order.builder()
                        .orderId("O200")
                        .itemIds(Arrays.asList("I200", "I201", "I202"))
                        .build());
        CustomerOrders customerOrders =
                CustomerOrders.builder().customerId("Saggu").orders(orders).build();
        template.sendBody("direct:customerOrder", customerOrders);
    }

    @Test
    void splitAndAggregateEip() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:aggregatedResult");
        mock.expectedBodiesReceived("A=Apple + B=Bucket + C=Cat");

        template.sendBody("direct:customerOrderAggregate", "A,B,C");

        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:start")
                        .log("Before Split line ${body}")
                        .split(body())
                        .delimiter("#")
                        .log("Split line ${body}")
                        .to("mock:split");

                // Complex Split
                from("direct:customerOrder")
                        .log("Customer Id: ${body.customerId}")
                        .split(method(OrderService.class))
                        .log("Order: ${body}");

                // Split and Aggregate
                from("direct:customerOrderAggregate")
                        .split(body(), new WordAggregationStrategy())
                        .stopOnException()
                        .bean(WordTranslateBean.class)
                        .to("mock:split")
                        .end()
                        .log("Aggregated ${body}")
                        .to("mock:aggregatedResult");
            }
        };
    }

    @Data
    @Builder
    static class CustomerOrders {
        private String customerId;
        private List<Order> orders;
    }

    @Data
    @Builder
    static class Order {
        private String orderId;
        private List<String> itemIds;
    }

    static class OrderService {
        public static List<Order> getOrders(CustomerOrders customerOrders) {
            return customerOrders.getOrders();
        }
    }
}
