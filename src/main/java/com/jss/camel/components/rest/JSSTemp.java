package com.jss.camel.components.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;

@RestController
public class JSSTemp {

    static int t=1;
    @GetMapping("hello")
    public String hello() throws InterruptedException {
        int i = t++;
//        System.out.println("[" + i + "] JSSGot it and now sleeping " + new Date());
        SECONDS.sleep(1);
//        System.out.println("[" + i + "] JSSGot it and Done " + new Date());
        return "[" + i + "]  Hello " + new Date();
    }


    public static void main(String[] args) {
        String payload = "JSSaggu";
        String wp = Base64.getEncoder().withoutPadding().encodeToString(payload.getBytes(UTF_8));
        String withPad = Base64.getEncoder().encodeToString(payload.getBytes(UTF_8));

        System.out.println(wp);
        System.out.println(withPad);

        long l = Long.parseLong("10.50");

        System.out.println(l);

    }
}
