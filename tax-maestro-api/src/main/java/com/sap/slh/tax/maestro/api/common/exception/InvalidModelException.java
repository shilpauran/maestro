package com.sap.slh.tax.maestro.api.common.exception;

import java.util.List;

public class InvalidModelException extends RuntimeException {

    private static final long serialVersionUID = -3168271820901926854L;

    private final List<PropertyErrorDetail> propertyErrors;

    public InvalidModelException(List<PropertyErrorDetail> propertyerrors) {
        super();
        this.propertyErrors = propertyerrors;
    }

    public List<PropertyErrorDetail> getPropertyErrors() {
        return propertyErrors;
    }

    public static void checkExceptions(List<PropertyErrorDetail> validationErrors) {
        if (validationErrors != null && !validationErrors.isEmpty()) {
            throw new InvalidModelException(validationErrors);
        }
    }
}
