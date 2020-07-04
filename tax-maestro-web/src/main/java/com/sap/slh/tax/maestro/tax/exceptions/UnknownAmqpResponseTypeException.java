package com.sap.slh.tax.maestro.tax.exceptions;

public class UnknownAmqpResponseTypeException extends TaxMaestroException {
    private static final long serialVersionUID = 6423409366068318310L;

    public UnknownAmqpResponseTypeException() {
        super("Unknown object received from AMQP queue");
    }
    
    public UnknownAmqpResponseTypeException(Throwable cause) {
        super("Unknown object received from AMQP queue", cause);
    }
}
