package com.sap.slh.tax.maestro.tax.exceptions.partner;

import com.sap.slh.tax.maestro.tax.exceptions.FailedHttpResponseException;

public class RequestTimeoutFailedHttpResponseException extends FailedHttpResponseException {

    private static final long serialVersionUID = 5412586301223331425L;

    public RequestTimeoutFailedHttpResponseException(String message) {
        super(message);
    }

}
