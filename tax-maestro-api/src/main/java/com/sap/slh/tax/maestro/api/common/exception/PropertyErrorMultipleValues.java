package com.sap.slh.tax.maestro.api.common.exception;

public class PropertyErrorMultipleValues extends PropertyErrorDetail {
    private static final String MSG_FORMAT = "You cannot have different values for property '%s' in a request";

    public PropertyErrorMultipleValues(String property) {
        super(property);
    }

    @Override
    public String getMessage() {
        return this.getMessageWithReferences(String.format(MSG_FORMAT, this.property));
    }

}
