package com.sap.slh.tax.maestro.api.common.exception;

public class PropertyErrorMandatoryValueMissingDetail extends PropertyErrorDetail {
    private static final String MSG_FORMAT = "Mandatory property missing: '%s' with value: '%s'";

    private final String suggestedValues;

    /**
     * {@link PropertyErrorMandatoryValueMissingDetail} constructor.
     * 
     * @param property
     *            property name
     * @param suggestedValues
     *            possible values suggested for the property
     */
    public PropertyErrorMandatoryValueMissingDetail(String property, String suggestedValues) {
        super(property);
        this.suggestedValues = suggestedValues;
    }

    public String getSuggestedValues() {
        return suggestedValues;
    }

    @Override
    public Object[] getAttributesAsArgs() {
        return new Object[] { this.property, this.getSuggestedValues() };
    }
    
    @Override
    public String getMessage() {
        return this.getMessageWithReferences(String.format(MSG_FORMAT, property, suggestedValues));
    }
}
