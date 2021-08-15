package com.jss.camel.components;

import com.jss.CamelApplication;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CamelApplication.class, properties = {"jss.camel.weather.enabled=true"})
@CamelSpringBootTest
@MockEndpoints()
class WeatherTest {

    @Test
    void startReadingQueues(){
    }
}