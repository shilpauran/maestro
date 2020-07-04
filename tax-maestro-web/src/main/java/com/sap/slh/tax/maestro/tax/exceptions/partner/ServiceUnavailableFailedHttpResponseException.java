package com.sap.slh.tax.maestro.tax.exceptions.partner;

import com.sap.slh.tax.maestro.tax.exceptions.FailedHttpResponseException;

public class ServiceUnavailableFailedHttpResponseException extends FailedHttpResponseException {

    private static final long serialVersionUID = 5412586301223331425L;

    public ServiceUnavailableFailedHttpResponseException(String message) {
        super(message);
    }

}
