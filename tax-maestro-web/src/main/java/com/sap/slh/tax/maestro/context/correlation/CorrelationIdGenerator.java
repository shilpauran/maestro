package com.sap.slh.tax.maestro.context.correlation;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class CorrelationIdGenerator {

    public String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

}
