package com.jss.routes.errorhandlers;

import static com.jss.routes.errorhandlers.CommonErrorHandlerRoute.COUNTER;

import java.util.Date;

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
