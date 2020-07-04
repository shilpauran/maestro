package com.sap.slh.tax.maestro.tax.exceptions.partner;

import com.sap.slh.tax.maestro.tax.exceptions.TaxMaestroException;

public class UnsupportedTokenTypeException extends TaxMaestroException {
    private static final long serialVersionUID = -2155513465663603571L;

    public UnsupportedTokenTypeException(String type, String destinationName) {
        super(String.format("The token type %s configured for Destination %s is not supported.", type,
                destinationName));
    }

    public UnsupportedTokenTypeException(String destinationName) {
        super(String.format("The token type for Destination %s is null.", destinationName));
    }

}