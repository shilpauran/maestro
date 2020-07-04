package com.sap.slh.tax.maestro.health.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController()
@RequestMapping(path = "/tax")
public class HealthController {

    private static final String JSON_STATUS_UP = "{\"status\": \"UP\"}";

    @GetMapping(path = "/ping", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Mono<ResponseEntity<String>> ping() {
        return Mono.just(ResponseEntity.ok().body(JSON_STATUS_UP));
    }

}