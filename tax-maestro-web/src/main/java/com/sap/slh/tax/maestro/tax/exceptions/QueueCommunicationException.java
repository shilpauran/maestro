package com.sap.slh.tax.maestro.tax.exceptions;

public class QueueCommunicationException extends TaxMaestroException {

    private static final long serialVersionUID = 1L;

    private final String errorMessage;

    public QueueCommunicationException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
