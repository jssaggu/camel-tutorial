package com.jss.camel.components.routes;

import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.PingHealthIndicator;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component
public class AllHealths {
    private final List<HealthIndicator> healthIndicators;
    private final HealthEndpoint healthEndpoint;

    public AllHealths(List<HealthIndicator> healthIndicators, HealthEndpoint healthEndpoint) {
        this.healthIndicators = healthIndicators;
        this.healthEndpoint = healthEndpoint;

        System.out.println("HealthEndpoint: " + this.healthEndpoint.health().getStatus());
        System.out.println("HealthEndpoint: " + this.healthEndpoint.healthForPath(PingHealthIndicator.class.getSimpleName()));

        System.out.println("JSS Healths: " + this.healthIndicators.size());
        System.out.println("-------");
        for (HealthIndicator c : healthIndicators) {
            System.out.println("  " + c.getClass().getSimpleName() + ": " + c.getHealth(false).getStatus());
        }
        System.out.println("---X---");
    }
}
