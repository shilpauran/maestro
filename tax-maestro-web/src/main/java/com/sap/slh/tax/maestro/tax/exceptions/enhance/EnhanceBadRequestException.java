package com.sap.slh.tax.maestro.tax.exceptions.enhance;

import java.util.List;

import com.sap.slh.tax.maestro.tax.exceptions.TaxMaestroException;

public class EnhanceBadRequestException extends TaxMaestroException {

    private static final long serialVersionUID = -8336472525845619389L;
    
    private final String errorMessage;
    private final List<String> errorDetails;

    public EnhanceBadRequestException(String msg, List<String> errorDetails) {
        super(msg);
        this.errorMessage = msg;
        this.errorDetails = errorDetails;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }

    public List<String> getErrorDetails() {
        return errorDetails;
    }
}
