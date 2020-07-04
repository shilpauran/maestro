package com.sap.slh.tax.maestro.tax.exceptions.determine;

import java.util.List;

import com.sap.slh.tax.maestro.tax.exceptions.TaxMaestroException;

public class DeterminePartialContentException extends TaxMaestroException {

    private static final long serialVersionUID = -1146180368318379593L;
    private final List<ErrorDetail> errorDetails;

    public DeterminePartialContentException(String msg, List<ErrorDetail> errorDetails) {
        super(msg);
        this.errorDetails = errorDetails;
    }
    
    public List<ErrorDetail> getErrorDetails() {
        return errorDetails;
    }

}
