package com.sap.slh.tax.maestro.context.correlation;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CorrelationIdGeneratorTest {

    @Test
    public void shouldGenerateUuid() {
        CorrelationIdGenerator correlationIdGenerator = new CorrelationIdGenerator();

        String generatedCorrelationId = correlationIdGenerator.generateCorrelationId();

        assertTrue(generatedCorrelationId.matches("([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})"));
    }

}
