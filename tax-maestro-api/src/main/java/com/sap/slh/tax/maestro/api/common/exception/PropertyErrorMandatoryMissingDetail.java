package com.sap.slh.tax.maestro.api.common.exception;

public class PropertyErrorMandatoryMissingDetail extends PropertyErrorDetail {
    private static final String MSG_FORMAT = "Mandatory property missing: '%s'";

    /**
     * {@link PropertyErrorMandatoryMissingDetail} constructor.
     * 
     * @param property property name
     */
    public PropertyErrorMandatoryMissingDetail(String property) {
        super(property);
    }

    @Override
    public String getMessage() {
        return this.getMessageWithReferences(String.format(MSG_FORMAT, property));
    }
}
