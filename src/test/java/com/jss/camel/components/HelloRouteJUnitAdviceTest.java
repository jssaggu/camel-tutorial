package com.jss.camel.components;

import com.jss.camel.components.routes.HelloRoute;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.camel.builder.AdviceWith.adviceWith;

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

        adviceWith(route, context,
                new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveAddLast().to("mock:finishGreeting");
                    }
                });

        context.start();

        MockEndpoint mock = getMockEndpoint("mock:finishGreeting");
        mock.expectedMessageCount(2);

        template.sendBody("direct:greeting", "Team");

        mock.assertIsSatisfied();
    }
}