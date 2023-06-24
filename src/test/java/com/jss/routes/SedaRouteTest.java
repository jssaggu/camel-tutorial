package com.jss.routes;

import com.jss.CamelApplication;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(
        classes = CamelApplication.class,
        properties = {"jss.camel.seda.enabled=true"})
@CamelSpringBootTest
@MockEndpoints()
public class SedaRouteTest {

    @Autowired private ProducerTemplate template;

    @Test
    @DirtiesContext
    public void testMocksAreValid() throws Exception {
        // template.sendBody("direct:ticker", "Hello");
    }
}
