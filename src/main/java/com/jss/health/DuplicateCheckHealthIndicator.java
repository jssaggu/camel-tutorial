package com.jss.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;

// @Component
public class DuplicateCheckHealthIndicator implements HealthIndicator {

    @Override
    public Health health(boolean includeDetails) {
        return HealthIndicator.super.health(includeDetails);
    }

    @Override
    public Health health() {
        Health health =
                new Health.Builder()
                        .withDetail("reason", "Unable to connect to Database")
                        .down()
                        .build();
        System.out.println("JSS Health: " + health);
        return health;
    }
}
