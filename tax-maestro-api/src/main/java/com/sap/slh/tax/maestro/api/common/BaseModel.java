package com.sap.slh.tax.maestro.api.common;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorMandatoryMissingDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorReferenceDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorValueOutOfBoundsDetail;

public abstract class BaseModel {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    @JsonIgnore
    public String getIndentifierValue() {
        return null;
    }

    /**
     * Validates if a class that extends {@link BaseModel} is in a valid state.
     * 
     * @throws PropertyErrorMandatoryMissingDetail
     *             If Model is in a invalid state because of missing mandatory attributes
     */
    public void validate() {

    }

    /**
     * Validates if a Collection is not null, is not empty and doesn't contains any null elements.
     * 
     * @param toValidate
     *            the property to be validated
     * @param propertyName
     *            the name of the property to be validated
     * @param missingProperties
     *            a List<String> that will be updated if the property is invalid
     * 
     */
    private <T extends Collection<?>> void validateMandatoryCollectionProperty(T toValidate, String propertyName,
            List<PropertyErrorDetail> missingProperties) {
        try {
            Validate.notEmpty(toValidate);
            Validate.noNullElements(toValidate);
        } catch (NullPointerException | IllegalArgumentException e) {
            missingProperties.add(new PropertyErrorMandatoryMissingDetail(propertyName));
        }
    }

    /**
     * Validates if a String is not null, is not empty and isn't blank.
     * 
     * @param toValidate
     *            the property to be validated
     * @param propertyName
     *            the name of the property to be validated
     * @param invalidProperty
     *            a List<String> that will be updated if the property is invalid
     */
    private <T extends CharSequence> void validateNotBlank(T toValidate, String propertyName,
            List<PropertyErrorDetail> invalidProperties) {
        try {
            Validate.notBlank(toValidate);
        } catch (NullPointerException | IllegalArgumentException e) {
            invalidProperties.add(new PropertyErrorMandatoryMissingDetail(propertyName));
        }
    }

    /**
     * Validates if a String is not null, is not empty and isn't blank.
     * 
     * @param toValidate
     *            the property to be validated
     * @param propertyName
     *            the name of the property to be validated
     * @param invalidProperty
     *            a List<String> that will be updated if the property is invalid
     */
    private <T extends CharSequence> void validateNotBlank(T toValidate, String propertyName,
            List<PropertyErrorDetail> invalidProperties, String listProperty, String identifierValue) {
        try {
            Validate.notBlank(toValidate);
        } catch (NullPointerException | IllegalArgumentException e) {

            PropertyErrorMandatoryMissingDetail error = new PropertyErrorMandatoryMissingDetail(propertyName);
            error.addReferencePropertyErrorDetail(new PropertyErrorReferenceDetail(listProperty, identifierValue));

            invalidProperties.add(error);
        }
    }

    /**
     * Validates if an Object is not null.
     * 
     * @param toValidate
     *            the property to be validated
     * @param propertyName
     *            the name of the property to be validated
     * @param invalidProperty
     *            a List<String> that will be updated if the property is invalid
     * @param listProperty
     *            the name of the list property to which the propertyName belongs
     * @param identifierValue
     *            the identifier on the listProperty to which the propertyName belongs
     */
    private void validateNotNull(Object toValidate, String propertyName, List<PropertyErrorDetail> invalidProperties) {
        try {
            Validate.notNull(toValidate);
        } catch (NullPointerException | IllegalArgumentException e) {
            invalidProperties.add(new PropertyErrorMandatoryMissingDetail(propertyName));
        }
    }
    
    /**
     * Validates if an Object is not null.
     * 
     * @param toValidate
     *            the property to be validated
     * @param propertyName
     *            the name of the property to be validated
     * @param invalidProperty
     *            a List<String> that will be updated if the property is invalid
     * @param listProperty
     *            the name of the list property to which the propertyName belongs
     * @param identifierValue
     *            the identifier on the listProperty to which the propertyName belongs
     */
    private void validateNotNull(Object toValidate, String propertyName, List<PropertyErrorDetail> invalidProperties,
            String listProperty, String identifierValue) {
        try {
            Validate.notNull(toValidate);
        } catch (NullPointerException | IllegalArgumentException e) {
            PropertyErrorMandatoryMissingDetail error = new PropertyErrorMandatoryMissingDetail(propertyName);
            error.addReferencePropertyErrorDetail(new PropertyErrorReferenceDetail(listProperty, identifierValue));

            invalidProperties.add(error);
        }
    }

    /**
     * If the Float is not null, validates if it is between zero and one hundred.
     * 
     * @param toValidate
     *            the property to be validated
     * @param propertyName
     *            the name of the property to be validated
     * @param invalidProperty
     *            a List<String> that will be updated if the property is invalid
     */
    protected void validatePercentageIsInRange(BigDecimal toValidate, String propertyName,
            List<PropertyErrorDetail> invalidProperties) {
        if (toValidate == null) {
            return;
        }

        if (BigDecimal.ZERO.compareTo(toValidate) > 0 || ONE_HUNDRED.compareTo(toValidate) < 0) {
            invalidProperties.add(new PropertyErrorValueOutOfBoundsDetail(propertyName, BigDecimal.ZERO.toString(),
                    ONE_HUNDRED.toString()));
        }
    }

    protected void validateMandatoryProperty(Integer toValidate, String propertyName,
            List<PropertyErrorDetail> missingProperties) {
        try {
            Validate.notNull(toValidate);
        } catch (NullPointerException | IllegalArgumentException e) {
            missingProperties.add(new PropertyErrorMandatoryMissingDetail(propertyName));
        }
    }

    protected void validateMandatoryProperty(Date toValidate, String propertyName,
            List<PropertyErrorDetail> missingProperties) {
        validateNotNull(toValidate, propertyName, missingProperties);
    }

    protected void validateMandatoryProperty(Object toValidate, String propertyName,
            List<PropertyErrorDetail> missingProperties, String listProperty, String identifierValue) {
        validateNotNull(toValidate, propertyName, missingProperties, listProperty, identifierValue);
    }

    protected void validateMandatoryProperty(Object toValidate, String propertyName,
            List<PropertyErrorDetail> missingProperties) {
        validateNotNull(toValidate, propertyName, missingProperties);
    }

    protected void validateMandatoryProperty(String toValidate, String propertyName,
            List<PropertyErrorDetail> missingProperties) {
        validateNotBlank(toValidate, propertyName, missingProperties);
    }

    protected void validateMandatoryProperty(String toValidate, String propertyName,
            List<PropertyErrorDetail> missingProperties, String listProperty, String identifierValue) {
        validateNotBlank(toValidate, propertyName, missingProperties, listProperty, identifierValue);
    }

    protected void validateMandatoryProperty(Enum<?> toValidate, String propertyName,
            List<PropertyErrorDetail> missingProperties) {
        validateNotNull(toValidate, propertyName, missingProperties);
    }

    protected void validateMandatoryProperty(BigDecimal toValidate, String propertyName,
            List<PropertyErrorDetail> missingProperties) {
        validateNotNull(toValidate, propertyName, missingProperties);
    }

    protected void validateMandatoryProperty(Boolean toValidate, String propertyName,
            List<PropertyErrorDetail> missingProperties) {
        validateNotNull(toValidate, propertyName, missingProperties);
    }

    protected <T> void mandatoryListProperty(List<T> toValidate, String propertyName,
            List<PropertyErrorDetail> missingProperties) {
        validateMandatoryCollectionProperty(toValidate, propertyName, missingProperties);
    }

    protected <T extends BaseModel> void validateListItems(List<T> toValidate, String propertyName,
            List<PropertyErrorDetail> missingProperties) {
        if (toValidate != null) {
            for (int i = 0; i < toValidate.size(); i++) {
                try {

                    T element = toValidate.get(i);
                    if (element != null) {
                        element.validate();
                    }
                } catch (InvalidModelException e) {
                    for (PropertyErrorDetail missingProperty : e.getPropertyErrors()) {
                        missingProperty.addReferencePropertyErrorDetail(new PropertyErrorReferenceDetail(propertyName,
                                toValidate.get(i).getIndentifierValue()));
                        missingProperties.add(missingProperty);
                    }
                }
            }
        }
    }

}
