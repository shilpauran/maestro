package com.sap.slh.tax.maestro.tax.exceptions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NoAmqpResponseExceptionTest {

    private String exchange1 = "Exchange1";
    private String routingKey1 = "RoutingKey1";
    private NoAmqpResponseException exception;

    @Test
    public void completeInstance() {
        exception = new NoAmqpResponseException(exchange1, routingKey1);

        assertEquals(exchange1, exception.getExchange());
        assertEquals(routingKey1, exception.getRoutingKey());

        String[] args = exception.getAttributesAsArgs();

        assertEquals(exchange1, args[0]);
        assertEquals(routingKey1, args[1]);
    }
}
