package com.sap.slh.tax.maestro.tax.exceptions.partner;

import com.sap.slh.tax.maestro.tax.exceptions.FailedHttpResponseException;

public class BadRequestFailedHttpResponseException extends FailedHttpResponseException {

    private static final long serialVersionUID = 477900910662562936L;
    private final String message;

    public BadRequestFailedHttpResponseException(String host, String message) {
        super(host);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
