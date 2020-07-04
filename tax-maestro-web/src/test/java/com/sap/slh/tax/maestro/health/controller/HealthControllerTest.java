package com.sap.slh.tax.maestro.health.controller;

import org.junit.Test;
import org.springframework.http.ResponseEntity;

import com.sap.slh.tax.maestro.health.controller.HealthController;

import reactor.test.StepVerifier;

public class HealthControllerTest {

    private static final String JSON_STATUS_UP = "{\"status\": \"UP\"}";

    private HealthController healthController = new HealthController();

    @Test
    public void ping() {
        StepVerifier.create(healthController.ping()).expectNext(ResponseEntity.ok().body(JSON_STATUS_UP))
                .verifyComplete();
    }

}