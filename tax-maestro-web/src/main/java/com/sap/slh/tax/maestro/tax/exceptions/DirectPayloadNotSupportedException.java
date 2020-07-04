package com.sap.slh.tax.maestro.tax.exceptions;

public class DirectPayloadNotSupportedException extends TaxMaestroException {

    private static final long serialVersionUID = 1L;

    public DirectPayloadNotSupportedException(String msg) {
        super(msg);
    }
}
