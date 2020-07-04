package com.sap.slh.tax.maestro.tax.exceptions;

public class NoRelevantCountryForDestinationException extends TaxMaestroException {

    private static final long serialVersionUID = 1L;

    public NoRelevantCountryForDestinationException(String msg) {
        super(msg);
    }
}
