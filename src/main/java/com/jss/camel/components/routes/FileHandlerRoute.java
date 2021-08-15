package com.jss.camel.components.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Scratch Pad
 * .convertBodyTo(String.class)
 * .process(e-> System.out.println(e.getMessage().getBody()))
 */
@Component
@ConditionalOnProperty(name = "jss.camel.file.enabled", havingValue = "true")
public class FileHandlerRoute extends RouteBuilder {
    public static final String FROM_DIR = "/Users/jasvinder.saggu/projects/temp/?noop=true&";
    public static final String TO_DIR = "/Users/jasvinder.saggu/projects/temp/?";
    public static final String APPEND = "fileExist=Append";

    public static void main(String[] args) {
        boolean enabled = false;
        Optional<Boolean> micrometerEnabled = Optional.of(enabled).filter(Boolean::booleanValue);

        micrometerEnabled.ifPresent(e -> System.out.println(enabled + " = " + micrometerEnabled));
        System.out.println("All: " + enabled + " = " + micrometerEnabled);

    }

    @Override
    public void configure() {
        System.out.println("In file...");
        CamelContext context = new DefaultCamelContext();
        /**
         * Copy data from one file to another.
         * Default behaviour Overwrite
         */
        from("file://" + FROM_DIR + "fileName=camel-demo-in.txt")
                .to("file://" + TO_DIR + "fileName=camel-demo-out.txt");

        /**
         * Append data to an existing file...
         */
        from("direct:appendToFile")
                .process(Exchange::getMessage)
                .to("file://" + TO_DIR + "fileName=camel-demo-appends.txt" + APPEND);
    }
}