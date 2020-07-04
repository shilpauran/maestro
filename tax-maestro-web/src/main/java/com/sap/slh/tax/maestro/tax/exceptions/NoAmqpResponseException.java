package com.sap.slh.tax.maestro.tax.exceptions;

public class NoAmqpResponseException extends TaxMaestroException {
    private static final long serialVersionUID = -2155513465663603571L;

    private final String exchange;
    private final String routingKey;

    public NoAmqpResponseException(String exchange, String routingKey) {
        super(String.format("No response received from exchange: %s and routing key: %s", exchange, routingKey));
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public String getExchange() {
        return this.exchange;
    }

    public String getRoutingKey() {
        return this.routingKey;
    }

    public String[] getAttributesAsArgs() {
        return new String[] { this.exchange, this.routingKey };
    }
}
