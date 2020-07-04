package com.sap.slh.tax.maestro.api.common.exception;

import java.util.Arrays;
import java.util.List;

public abstract class PropertyErrorDetail {
    protected String property;
    protected PropertyErrorDetail referencePropertyErrorDetail;

    protected PropertyErrorDetail(String property) {
        this.property = property;
    }

    public abstract String getMessage();

    public String getProperty() {
        return property;
    }

    public PropertyErrorDetail getReferencePropertyErrorDetail() {
        return referencePropertyErrorDetail;
    }

    public void addReferencePropertyErrorDetails(PropertyErrorDetail... referencePropertyErrorDetails) {
        this.addReferencePropertyErrorDetails(Arrays.asList(referencePropertyErrorDetails));
    }

    public void addReferencePropertyErrorDetails(List<PropertyErrorDetail> referencePropertyErrorDetails) {
        for (PropertyErrorDetail propertyErrorDetail : referencePropertyErrorDetails) {
            this.addReferencePropertyErrorDetail(propertyErrorDetail);
        }
    }

    public void addReferencePropertyErrorDetail(PropertyErrorDetail referencePropertyErrorDetail) {
        if (this.referencePropertyErrorDetail == null) {
            this.referencePropertyErrorDetail = referencePropertyErrorDetail;
        } else {
            this.referencePropertyErrorDetail.addReferencePropertyErrorDetail(referencePropertyErrorDetail);
        }
    }

    public String getMessageWithReferences(String message) {
        StringBuilder sb = new StringBuilder();
        sb.append(message);
        if (this.referencePropertyErrorDetail != null) {
            sb.append(this.referencePropertyErrorDetail.getMessage());
        }
        return sb.toString();
    }

    public Object[] getAttributesAsArgs() {
        return new Object[] { this.property };
    }
}
