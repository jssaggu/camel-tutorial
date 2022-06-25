package com.jss.camel.components.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jss.camel.components.routes.rabbitmq.WeatherRoute.SERVER_REST_SLEEP_SECONDS;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;

@RestController
public class JSSTemp {

    static AtomicInteger t = new AtomicInteger(0);

    @GetMapping("hello/{id}")
    public String hello(@PathVariable String id) throws InterruptedException {
        final int i = t.incrementAndGet();
        long sleepTime = new Random().nextInt(SERVER_REST_SLEEP_SECONDS);
        System.out.println("[" + id + " " + i + "] JSSGot it and now sleeping (" + sleepTime + ")" + new Date());
        SECONDS.sleep(sleepTime);
//        System.out.println("[" + i + "] JSSGot it and Done " + new Date());
        return "[" + id +" " + i + "]  Hello " + new Date();
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
