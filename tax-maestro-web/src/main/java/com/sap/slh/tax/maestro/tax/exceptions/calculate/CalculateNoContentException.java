package com.sap.slh.tax.maestro.tax.exceptions.calculate;

import java.util.List;

import com.sap.slh.tax.maestro.tax.exceptions.TaxMaestroException;

public class CalculateNoContentException extends TaxMaestroException {

    private static final long serialVersionUID = -3197363887441001115L;
    private final List<ErrorDetail> errorDetails;

    public CalculateNoContentException(String msg, List<ErrorDetail> errorDetails) {
        super(msg);
        this.errorDetails = errorDetails;
    }
    
    public List<ErrorDetail> getErrorDetails() {
        return errorDetails;
    }

}
