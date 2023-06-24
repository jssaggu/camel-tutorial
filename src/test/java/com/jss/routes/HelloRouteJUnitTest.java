package com.jss.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

public class HelloRouteJUnitTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new HelloRoute();
    }

    @Test
    public void testMocksAreValid() throws InterruptedException {
        System.out.println("Sending 1");
        template.sendBody("direct:greeting", "Team");

        System.out.println("Sending 2");
        template.sendBody("direct:greeting", "Me");
    }
}
