package com.sap.slh.tax.maestro.tax.exceptions.partner;

import com.sap.slh.tax.maestro.tax.exceptions.FailedHttpResponseException;

public class GatewayTimeoutFailedHttpResponseException extends FailedHttpResponseException {

    private static final long serialVersionUID = 5412586301223331425L;

    public GatewayTimeoutFailedHttpResponseException(String message) {
        super(message);
    }

}
