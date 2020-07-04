package com.sap.slh.tax.maestro.api.common.exception;

import org.apache.commons.lang3.StringUtils;

public class PropertyErrorReferenceDetail extends PropertyErrorDetail {

    private String errorLocationPropertyId;
    
    public PropertyErrorReferenceDetail(String property, String errorLocationPropertyId) {
        super(property);
        this.errorLocationPropertyId = errorLocationPropertyId;
    }
    
    public PropertyErrorReferenceDetail(String property) {
        super(property);
    }
    
    public String getErrorLocationPropertyId() {
        return errorLocationPropertyId;
    }

    @Override
    public Object[] getAttributesAsArgs() {
        if (StringUtils.isNotBlank(this.errorLocationPropertyId)) {
            return new Object[] { this.property, this.errorLocationPropertyId};
        } else {
            return new Object[] { this.property };
        }
    }
    
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        
        if (property != null) {
            sb.append(String.format(" under %s", property));
        }
        if (errorLocationPropertyId != null) {
            sb.append(String.format(" %s", errorLocationPropertyId));
        }
        if (referencePropertyErrorDetail != null) {
            sb.append(referencePropertyErrorDetail.getMessage());
        }
        
        return sb.toString();
    }
}
