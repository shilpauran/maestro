package com.sap.slh.tax.maestro.tax.exceptions;

public class NoJwtProvidedException extends TaxMaestroException {

    private static final long serialVersionUID = 1L;

    public NoJwtProvidedException() {
        super("No JWT provided on header");
    }
}
