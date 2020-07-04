package com.sap.slh.tax.maestro.tax.exceptions.calculate;

import java.util.List;

import com.sap.slh.tax.maestro.tax.exceptions.TaxMaestroException;

public class CalculateInvalidModelException extends TaxMaestroException {

    private static final long serialVersionUID = 8473638031447747671L;
    private final List<ErrorDetail> errorDetails;

    public CalculateInvalidModelException(String detailMessage, List<ErrorDetail> errorDetails) {
        super(detailMessage);
        this.errorDetails = errorDetails;
    }
    
    public List<ErrorDetail> getErrorDetails() {
        return errorDetails;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((errorDetails == null) ? 0 : errorDetails.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof CalculateInvalidModelException))
            return false;
        CalculateInvalidModelException other = (CalculateInvalidModelException)obj;
        if (errorDetails == null) {
            if (other.errorDetails != null)
                return false;
        } else if (!errorDetails.equals(other.errorDetails))
            return false;
        return true;
    }
}
