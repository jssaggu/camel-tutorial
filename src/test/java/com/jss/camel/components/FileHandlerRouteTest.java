package com.jss.camel.components;

import com.jss.CamelApplication;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Date;

@SpringBootTest(classes = CamelApplication.class, properties = {"jss.camel.file.enabled=false"})
@CamelSpringBootTest
@MockEndpoints()
public class FileHandlerRouteTest {

    @Autowired
    private ProducerTemplate template;

    @Test
    @DirtiesContext
    public void testCamelFileRoute() {
        System.out.println("Sending request to append to existing file...");
        //TODO Fix to run in CI build
        // Issue: Temp file not created
        template.sendBody("direct:appendToFile", "Hello " + new Date() + "\n");
        System.out.println("Sent request to append to existing file...");
    }
}