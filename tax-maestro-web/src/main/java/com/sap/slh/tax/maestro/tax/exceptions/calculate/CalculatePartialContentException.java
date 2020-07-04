package com.sap.slh.tax.maestro.tax.exceptions.calculate;

import java.util.List;

import com.sap.slh.tax.maestro.tax.exceptions.TaxMaestroException;

public class CalculatePartialContentException extends TaxMaestroException {

    private static final long serialVersionUID = -1146180368318379593L;
    private final List<ErrorDetail> errorDetails;

    public CalculatePartialContentException(String msg, List<ErrorDetail> errorDetails) {
        super(msg);
        this.errorDetails = errorDetails;
    }

    public List<ErrorDetail> getErrorDetails() {
        return errorDetails;
    }
    
}
