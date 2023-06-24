package com.jss.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

class ComposedMessageProcessorTest extends CamelTestSupport {

    @Override
    public RouteBuilder createRouteBuilder() {
        return new ComposedMessageProcessor();
    }

    @Test
    void sendMessage() {

        template.sendBody("direct:startCMP", "Aaa@Bbb@Ccc@Ddd");
    }
}
