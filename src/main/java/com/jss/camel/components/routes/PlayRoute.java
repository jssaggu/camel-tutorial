package com.jss.camel.components.routes;

import com.jss.camel.components.routes.temp.JSSBean;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Date;

import static org.apache.camel.LoggingLevel.ERROR;

//@Component
public class PlayRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:time?period=5000")
                .process(exchange -> exchange.getIn().setBody(new Date()))
//                .process(exchange -> exchange.getIn().getHeaders())
                .bean(HelloBean.class, "get2")
//                .log(ERROR, ">> ${header.firedTime} >> ${body}")
        .to("log:reply")
                ;
    }

    private static class HelloBean {
        public HelloBean() {
            System.out.println("HellBean Constructor");
        }
        public String get() {
            System.out.println("Hello Bean1..." + new Date());
            return "HelloBean1::" + new Date();
        }

        public String get2(String firedTime) {
            System.out.println("Hello Bean2..." + firedTime);
            return "HelloBean2::" + new Date();
        }
    }

    @Bean("jssSagguBean")
    public ConnectionFactory jsSagguBean() {
//        return new JSSBean("Using jsSagguBean");
        return new ConnectionFactory();
    }
}
