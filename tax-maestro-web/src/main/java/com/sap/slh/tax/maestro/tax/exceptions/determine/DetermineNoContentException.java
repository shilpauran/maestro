package com.sap.slh.tax.maestro.tax.exceptions.determine;

import java.util.List;

import com.sap.slh.tax.maestro.tax.exceptions.TaxMaestroException;

public class DetermineNoContentException extends TaxMaestroException {

    private static final long serialVersionUID = -3197363887441001115L;
    private final List<ErrorDetail> errorDetails;

    public DetermineNoContentException(String msg, List<ErrorDetail> errorDetails) {
        super(msg);
        this.errorDetails = errorDetails;
    }
    
    public List<ErrorDetail> getErrorDetails() {
        return errorDetails;
    }

}
