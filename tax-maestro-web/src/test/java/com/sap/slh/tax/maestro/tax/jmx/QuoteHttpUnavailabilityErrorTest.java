package com.sap.slh.tax.maestro.tax.jmx;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class QuoteHttpUnavailabilityErrorTest {

    private CalculateQueueIntegrationErrorMBean jmx;

    @Before
    public void setup() {
        jmx = new CalculateQueueIntegrationErrorMBean();
    }

    @Test
    public void increment() {
        jmx.incrementCount();
        assertEquals(1, jmx.getCount());
    }

    @Test
    public void reset() {
        jmx.incrementCount();
        jmx.resetCount();
        assertEquals(0, jmx.getCount());
    }

}