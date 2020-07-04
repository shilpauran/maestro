package com.sap.slh.tax.maestro.tax.exceptions.partner;

import com.sap.slh.tax.maestro.tax.exceptions.FailedHttpResponseException;

public class FailedRequestFailedHttpResponseException extends FailedHttpResponseException {
    private static final long serialVersionUID = -2155513465663603571L;

    public FailedRequestFailedHttpResponseException(String host) {
        super(host);
    }

}
