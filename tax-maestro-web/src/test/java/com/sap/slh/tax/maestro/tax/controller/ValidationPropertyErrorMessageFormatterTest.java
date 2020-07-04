package com.sap.slh.tax.maestro.tax.controller;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorMandatoryMissingDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorMandatoryValueMissingDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorMissingReferenceDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorMutualExclusionDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorReferenceDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorValueOutOfBoundsDetail;

public class ValidationPropertyErrorMessageFormatterTest {

    private static ValidationPropertyErrorMessageFormatter formatter;

    @BeforeClass
    public static void classSetup() {
        MessageSourceMock msgSource = new MessageSourceMock();
        formatter = new ValidationPropertyErrorMessageFormatter(msgSource);
    }

    @Test
    public void singleMandatoryPropertyError() {
        List<PropertyErrorDetail> errorDetails = Arrays.asList(new PropertyErrorMandatoryMissingDetail("Property"));

        List<String> expected = Arrays.asList("The 'Property' parameter is mandatory in a request.");

        assertEquals(expected, formatter.getPropertyErrorsMessages(errorDetails, Locale.US));
    }

    @Test
    public void singleMandatoryValuePropertyError() {
        List<PropertyErrorDetail> errorDetails = Arrays
                .asList(new PropertyErrorMandatoryValueMissingDetail("Property", "[value1, value2]"));

        List<String> expected = Arrays.asList("The 'Property' parameter, with one of the following values: [value1, value2], is mandatory in this request. Add this parameter in an entry.");

        assertEquals(expected, formatter.getPropertyErrorsMessages(errorDetails, Locale.US));
    }

    @Test
    public void singleOutOfBoundsPropertyError() {
        List<PropertyErrorDetail> errorDetails = Arrays
                .asList(new PropertyErrorValueOutOfBoundsDetail("Property", "value1", "value2"));

        List<String> expected = Arrays
                .asList("Enter a value that is between value1 and value2 for the 'Property' parameter.");

        assertEquals(expected, formatter.getPropertyErrorsMessages(errorDetails, Locale.US));
    }

    @Test
    public void singleMissingReferencePropertyError() {
        List<PropertyErrorDetail> errorDetails = Arrays
                .asList(new PropertyErrorMissingReferenceDetail("Property", "value1"));

        List<String> expected = Arrays.asList("Enter reference details for the 'Property' parameter with the 'value1' ID.");

        assertEquals(expected, formatter.getPropertyErrorsMessages(errorDetails, Locale.US));
    }

    @Test
    public void singleMutualExclusionPropertyError() {
        List<PropertyErrorDetail> errorDetails = Arrays
                .asList(new PropertyErrorMutualExclusionDetail("products", Arrays.asList("value1", "value2")));

        List<String> expected = Arrays
                .asList("Provide only one of the following attributes for the 'products' parameter: [value1; value2].");

        assertEquals(expected, formatter.getPropertyErrorsMessages(errorDetails, Locale.US));
    }

    @Test
    public void multiLevelPropertyError() {
        PropertyErrorMandatoryValueMissingDetail error = new PropertyErrorMandatoryValueMissingDetail("Property",
                "[value1, value2]");

        error.addReferencePropertyErrorDetails(new PropertyErrorReferenceDetail("List", "1"),
                new PropertyErrorReferenceDetail("List", "2"), new PropertyErrorReferenceDetail("List", "3"));
        List<PropertyErrorDetail> errors = Arrays.asList(error);

        List<String> expected = Arrays.asList(
                "The 'Property' parameter, with one of the following values: [value1, value2], is mandatory in this request. Add this parameter in an entry, under 'List' 1, under 'List' 2, under 'List' 3.");

        assertEquals(expected, formatter.getPropertyErrorsMessages(errors, Locale.US));
    }

    @Test
    public void multiplePropertyError() {
        PropertyErrorMandatoryValueMissingDetail error1 = new PropertyErrorMandatoryValueMissingDetail("Property",
                "[value1, value2]");
        PropertyErrorMandatoryMissingDetail error2 = new PropertyErrorMandatoryMissingDetail("Property");
        PropertyErrorMandatoryMissingDetail error3 = new PropertyErrorMandatoryMissingDetail("Property");
        PropertyErrorMutualExclusionDetail error4 = new PropertyErrorMutualExclusionDetail("Property",
                Arrays.asList("value1", "value2"));

        error1.addReferencePropertyErrorDetails(new PropertyErrorReferenceDetail("List", "1"),
                new PropertyErrorReferenceDetail("List", "2"), new PropertyErrorReferenceDetail("List", "3"));
        error2.addReferencePropertyErrorDetails(new PropertyErrorReferenceDetail("List"));

        List<PropertyErrorDetail> errors = Arrays.asList(error1, error2, error3, error4);

        List<String> expected = Arrays.asList(
                "The 'Property' parameter, with one of the following values: [value1, value2], is mandatory in this request. Add this parameter in an entry, under 'List' 1, under 'List' 2, under 'List' 3.",
                "The 'Property' parameter is mandatory in a request, under 'List'.",
                "The 'Property' parameter is mandatory in a request.",
                "Provide only one of the following attributes for the 'Property' parameter: [value1; value2].");

        assertEquals(expected, formatter.getPropertyErrorsMessages(errors, Locale.US));
        assertEquals(String.join(" ", expected), formatter.getPropertyErrorsSingleMessage(errors, Locale.US));
        assertArrayEquals(new Object[] { String.join(" ", expected) },
                formatter.getPropertyErrorsSingleMessageAsArgs(errors, Locale.US));
    }
}
