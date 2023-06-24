package com.jss.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class MaskSensitiveInfo implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String line = exchange.getMessage().getBody(String.class);

        if (line.toLowerCase().startsWith("password:") || line.toLowerCase().startsWith("pwd:")) {
            line = line.substring(0, line.indexOf(":")) + ": *****";
            exchange.getMessage().setBody(line);
        }
    }
}
