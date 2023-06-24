package com.jss.routes.contentbasedrouter;

import static org.apache.camel.LoggingLevel.ERROR;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "jss.camel.choice.enabled", havingValue = "true")
public class ChoiceRoute extends RouteBuilder {

    public static final String WIDGET = "widget";
    public static final String GADGET = "gadget";
    public static final String GENERAL = "general";
    public static final String INVENTORY = "inventory";
    private static final String HEADER_INVENTORY = "${header." + INVENTORY + "}";

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("direct:orders")
                .choice()
                .when(simple(HEADER_INVENTORY + " == '" + WIDGET + "'"))
                .to("direct:widget")
                .when(simple(HEADER_INVENTORY + " == '" + GADGET + "'"))
                .to("direct:gadget")
                .otherwise()
                .to("direct:general");

        from("direct:widget").routeId(WIDGET).log(ERROR, "Got a " + WIDGET + "order for ${body}");

        from("direct:gadget").routeId(GADGET).log(ERROR, "Got a " + GADGET + " order for ${body}");

        from("direct:general")
                .routeId(GENERAL)
                .log(ERROR, "Got a " + GENERAL + " order for ${body}");

        // @formatter:on
    }
}
