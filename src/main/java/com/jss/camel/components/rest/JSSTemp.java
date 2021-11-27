package com.jss.camel.components.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class JSSTemp {

    @GetMapping("hello")
    public String hello() {
        return "Hello " + new Date();
    }
}
