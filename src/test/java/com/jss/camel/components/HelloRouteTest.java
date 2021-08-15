package com.jss.camel.components;

import com.jss.CamelApplication;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CamelApplication.class, properties = {"jss.camel.hello.enabled=true"})
@CamelSpringBootTest
@MockEndpoints()
public class HelloRouteTest extends CamelTestSupport {

    @Autowired
    private ProducerTemplate template;

    @Test
    public void testMocksAreValid() {
        System.out.println("Sending 1");
        template.sendBody("direct:greeting", "Team");
        System.out.println("Sending 2");
        template.sendBody("direct:greeting", "Me");

    }
}