package com.jss.routes;

import java.util.Optional;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Scratch Pad .convertBodyTo(String.class) .process(e->
 * System.out.println(e.getMessage().getBody()))
 */
@Component
@ConditionalOnProperty(name = "jss.camel.file.enabled", havingValue = "true")
public class FileHandlerRoute extends RouteBuilder {
    public static final String APPEND = "&fileExist=Append";
    //    private static String TMP_DIR = System.getProperty("java.io.tmpdir");
    private static String TMP_DIR = "/tmp/";
    //    public static final String FROM_DIR = TMP_DIR + "camel/?noop=true&";
    public static final String FROM_DIR = TMP_DIR + "camel/?";
    public static final String TO_DIR = TMP_DIR + "camel/?";

    public static void main(String[] args) {
        boolean enabled = false;
        Optional<Boolean> micrometerEnabled = Optional.of(enabled).filter(Boolean::booleanValue);

        micrometerEnabled.ifPresent(e -> System.out.println(enabled + " = " + micrometerEnabled));
        System.out.println("All: " + enabled + " = " + micrometerEnabled);
    }

    @Override
    public void configure() {
        System.out.println("TMP_DIR: " + FROM_DIR);
        /** Copy data from one file to another. Default behaviour Overwrite */
        from("file://" + FROM_DIR + "fileName=camel-demo-in.txt")
                .log(LoggingLevel.ERROR, ">> ${body}")
                .process(new MaskSensitiveInfo())
                .to("file://" + TO_DIR + "fileName=camel-demo-out.txt" + APPEND);

        /** Append data to an existing file... */
        from("direct:appendToFile")
                .process(Exchange::getMessage)
                .to("file://" + TO_DIR + "fileName=camel-demo-appends.txt" + APPEND);
    }
}
