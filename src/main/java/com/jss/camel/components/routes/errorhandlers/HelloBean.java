package com.jss.camel.components.routes.errorhandlers;

import java.util.Date;

import static com.jss.camel.components.routes.errorhandlers.CommonErrorHandlerRoute.COUNTER;

public class HelloBean {
    public HelloBean() {
        System.out.println("HellBean Constructor");
    }

    public String callGood() {
        System.out.println("Good Call for " + COUNTER.get());
        return "Good:" + new Date();
    }

    public String callBad() {
        System.out.println("Bad Call for " + COUNTER.get());
        throw new RuntimeException("Exception for " + COUNTER.get());
    }
}