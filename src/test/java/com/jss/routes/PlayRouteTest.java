package com.jss.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

class PlayRouteTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new PlayRoute();
    }

    @Test
    void callDirect() {

        template.sendBody("direct:start", loadComplexMessage());

        printMock("mock:step-1");
        printMock("mock:step-2");
        printMock("mock:step-3");
        printMock("mock:step-4");
        printMock("mock:step-5");
        printMock("mock:step-6");
    }

    private String loadComplexMessage() {
        String str =
                """
                {
                    "name": "JSingh",
                    "city": "London",
                    "attachment": "binary-large-data-xxxxxdddddd1213sdsdsdsdd"
                }
                """;
        return str;
    }

    void printMock(String route) {
        MockEndpoint mock = getMockEndpoint(route);
        System.out.println(
                "Mock ["
                        + route
                        + "]: "
                        + mock.getReceivedExchanges().get(0).getMessage().getBody());
    }
}
