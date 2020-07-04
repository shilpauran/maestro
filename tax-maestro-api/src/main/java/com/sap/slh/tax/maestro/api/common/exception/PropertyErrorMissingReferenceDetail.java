package com.sap.slh.tax.maestro.api.common.exception;

public class PropertyErrorMissingReferenceDetail extends PropertyErrorDetail {
    private static final String MSG_FORMAT = "Missing reference details for property '%s' with id '%s'";

    private String referenceId;

    /**
     * {@link PropertyErrorMissingReferenceDetail} constructor.
     * 
     * @param property property name
     * @param referenceId the id of the missing reference
     */
    public PropertyErrorMissingReferenceDetail(String property, String referenceId) {
        super(property);
        this.referenceId = referenceId;
    }

    public String getReferenceId() {
        return this.referenceId;
    }

    @Override
    public Object[] getAttributesAsArgs() {
        return new Object[] { this.property, this.referenceId };
    }
    
    @Override
    public String getMessage() {
        return this.getMessageWithReferences(String.format(MSG_FORMAT, property, referenceId));
    }
}
