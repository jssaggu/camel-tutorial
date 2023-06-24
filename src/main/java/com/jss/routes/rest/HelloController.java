package com.jss.routes.rest;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Date;
import java.util.Random;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class can be used as a ping service. It takes sleepTimeMills param which is used to sleep
 * the request for a random ms between 0 and sleepTimeMills. e.g.
 * http://localhost:8080/hello?sleepTimeMills=1000
 */
@RestController
public class HelloController {

    @GetMapping("hello")
    public String hello(@RequestParam String sleepTimeMills) {
        Date requestReceived = new Date();
        Integer randomTime = randomTime(sleepTimeMills);
        if (randomTime >= 500) {
            System.out.println("Long Sleep (" + randomTime + ") Request for: " + sleepTimeMills);
        }
        try {
            MILLISECONDS.sleep(randomTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "Response: Sleep("
                + randomTime
                + ") Start("
                + requestReceived
                + ") (End: "
                + new Date()
                + ")";
    }

    private Integer randomTime(String bound) {
        Random random = new Random();
        return random.nextInt(Integer.valueOf(bound));
    }
}
