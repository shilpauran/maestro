package com.sap.slh.tax.maestro.tax.exceptions;

public abstract class TaxMaestroException extends RuntimeException {

    private static final long serialVersionUID = 2155228952038822047L;

    public TaxMaestroException(String msg) {
        super(msg);
    }
    
    public TaxMaestroException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
