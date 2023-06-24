package com.jss.routes;

import com.jss.CamelApplication;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = CamelApplication.class,
        properties = {"jss.camel.hello.enabled=true"})
public class HelloRouteSpringTest {

    @Autowired private ProducerTemplate template;

    @Test
    void testMocksAreValid() {
        System.out.println("Sending 1");
        template.sendBody("direct:greeting", "Team");
        System.out.println("Sending 2");
        template.sendBody("direct:greeting", "Me");
    }
}
