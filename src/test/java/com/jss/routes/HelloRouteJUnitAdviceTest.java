package com.jss.routes;

import static org.apache.camel.builder.AdviceWith.adviceWith;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

public class HelloRouteJUnitAdviceTest extends CamelTestSupport {

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new HelloRoute();
    }

    @Test
    public void testMockEndpoints() throws Exception {
        RouteDefinition route = context.getRouteDefinition("greeting");

        adviceWith(
                route,
                context,
                new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveAddLast().to("mock:finishGreeting");
                    }
                });

        context.start();

        MockEndpoint mock = getMockEndpoint("mock:finishGreeting");
        mock.expectedMessageCount(1);

        template.sendBody("direct:greeting", "Team");

        mock.assertIsSatisfied();
    }
}
