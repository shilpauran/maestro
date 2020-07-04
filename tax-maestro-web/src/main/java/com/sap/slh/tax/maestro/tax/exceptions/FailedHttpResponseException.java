package com.sap.slh.tax.maestro.tax.exceptions;

public class FailedHttpResponseException extends TaxMaestroException {
    private static final long serialVersionUID = -2155513465663603571L;

    private final String url;

    public FailedHttpResponseException(String url) {
        super(String.format("Error from url: %s", url));
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public String[] getAttributesAsArgs() {
        return new String[] { this.url};
    }
}
