package com.sap.slh.tax.maestro.tax.jmx;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class DestinationQueueIntegrationErrorMBeanTest {

    private DestinationQueueIntegrationErrorMBean jmx;

    @Before
    public void setup() {
        jmx = new DestinationQueueIntegrationErrorMBean();
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