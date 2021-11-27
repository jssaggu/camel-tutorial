package com.jss.health;

import org.apache.camel.CamelContext;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

//@Component
public class PZCamelHealthIndicator implements HealthIndicator {

    private final CamelContext camelContext;

    protected PZCamelHealthIndicator(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    @Override
    public Health getHealth(boolean includeDetails) {
        return HealthIndicator.super.getHealth(includeDetails);
    }

    @Override
    public Health health() {

        Health.Builder health;

        if (camelContext.isStarted()) {
            health = Health.up();
        } else {
            health = Health.down();
        }
        System.out.println("Camel Health: " + health.build());
        return health.build();
    }
}