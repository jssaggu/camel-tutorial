package com.jss.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

//@Component
public class DuplicateCheckHealthIndicator implements HealthIndicator {

    @Override
    public Health getHealth(boolean includeDetails) {
        return HealthIndicator.super.getHealth(includeDetails);
    }

    @Override
    public Health health() {
        Health health = new Health.Builder().withDetail("reason", "Unable to connect to Database").down().build();
        System.out.println("JSS Health: " + health);
        return health;
    }
}