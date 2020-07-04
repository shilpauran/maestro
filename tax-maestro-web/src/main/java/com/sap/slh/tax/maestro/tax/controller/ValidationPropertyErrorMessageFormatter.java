package com.sap.slh.tax.maestro.tax.controller;

import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorMandatoryMissingDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorMandatoryValueMissingDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorMissingReferenceDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorMultipleValues;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorMutualExclusionDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorReferenceDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorValueOutOfBoundsDetail;
import com.sap.slh.tax.maestro.i18n.I18N;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ValidationPropertyErrorMessageFormatter {

    private MessageSource msgSource;

    public ValidationPropertyErrorMessageFormatter(MessageSource msgSource) {
        this.msgSource = msgSource;
    }

    private I18N getMessageId(PropertyErrorDetail propertyError) {
        if (propertyError instanceof PropertyErrorReferenceDetail) {
            if (StringUtils.isNotBlank(((PropertyErrorReferenceDetail)propertyError).getErrorLocationPropertyId())) {
                return I18N.MESSAGE_INVALID_PROPERTY_REFERENCE_WITH_ID;
            } else {
                return I18N.MESSAGE_INVALID_PROPERTY_REFERENCE;
            }
        } else if (propertyError instanceof PropertyErrorMandatoryMissingDetail) {
            return I18N.MESSAGE_INVALID_PROPERTY_MANDATORY;
        } else if (propertyError instanceof PropertyErrorMandatoryValueMissingDetail) {
            return I18N.MESSAGE_INVALID_PROPERTY_MANDATORY_VALUE;
        } else if (propertyError instanceof PropertyErrorValueOutOfBoundsDetail) {
            return I18N.MESSAGE_INVALID_PROPERTY_OUT_OF_BOUNDS;
        } else if (propertyError instanceof PropertyErrorMutualExclusionDetail) {
            return I18N.MESSAGE_MUTUAL_PROPERTY_EXCLUSION;
        } else if (propertyError instanceof PropertyErrorMissingReferenceDetail) {
            return I18N.MESSAGE_INVALID_PROPERTY_MISSING_REFERENCE;
        } else if (propertyError instanceof PropertyErrorMultipleValues) {
            return I18N.MESSAGE_MULTIPLE_VALUES_FOR_PROPERTY;
        } else {
            return I18N.MESSAGE_INVALID_REQUEST_EXCEPTION;
        }
    }

    public Object[] getPropertyErrorsSingleMessageAsArgs(List<PropertyErrorDetail> propertyErrors, Locale locale) {
        return new Object[]{this.getPropertyErrorsSingleMessage(propertyErrors, locale)};
    }

    public List<String> getPropertyErrorsMessages(List<PropertyErrorDetail> propertyErrors, Locale locale) {
        List<String> messages = new ArrayList<>();
        for (PropertyErrorDetail error : propertyErrors) {
            messages.add(this.getMessage(error, locale));
        }
        return messages;
    }

    public String getPropertyErrorsSingleMessage(List<PropertyErrorDetail> propertyErrors, Locale locale) {
        return String.join(" ", this.getPropertyErrorsMessages(propertyErrors, locale));
    }

    public String getMessage(PropertyErrorDetail propertyError, Locale locale) {
        StringBuilder sb = new StringBuilder();

        sb.append(msgSource.getMessage(this.getMessageId(propertyError).getValue(), propertyError.getAttributesAsArgs(), locale));

        if (propertyError.getReferencePropertyErrorDetail() == null) {
            sb.append('.');
        }

        if (propertyError.getReferencePropertyErrorDetail() != null) {
            sb.append(this.getMessage(propertyError.getReferencePropertyErrorDetail(), locale));
        }

        return sb.toString();
    }
}
