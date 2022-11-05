package com.jss.camel.components.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


@RestController
public class JSSTemp {

    @GetMapping("hello")
    public String hello(@RequestParam String sleepTimeMills) {
        try {
            MILLISECONDS.sleep(Long.parseLong(sleepTimeMills));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "Hello " + new Date();
    }
}
