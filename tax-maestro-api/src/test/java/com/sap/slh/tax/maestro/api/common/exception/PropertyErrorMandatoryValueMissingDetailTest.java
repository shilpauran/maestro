package com.sap.slh.tax.maestro.api.common.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PropertyErrorMandatoryValueMissingDetailTest {

    private String mandatoryPropertyMessageFormat = "Mandatory property missing: '%s' with value: '%s'";
    private String mandatoryPropertyDetailedMessageFormat = "Mandatory property missing: '%s' with value: '%s' under %s";

    @Test
    public void simplePropertyValueError() {
        PropertyErrorMandatoryValueMissingDetail error = new PropertyErrorMandatoryValueMissingDetail("property",
                "[A, B, C]");

        String expectedMessage = String.format(mandatoryPropertyMessageFormat, "property", "[A, B, C]");
        assertEquals(expectedMessage, error.getMessage());
    }

    @Test
    public void detailedPropertyValueError() {
        PropertyErrorMandatoryValueMissingDetail error = new PropertyErrorMandatoryValueMissingDetail("property",
                "[A, B, C]");
        PropertyErrorReferenceDetail ref1 = new PropertyErrorReferenceDetail("list");
        error.addReferencePropertyErrorDetail(ref1);

        String expectedMessage = String.format(mandatoryPropertyDetailedMessageFormat, "property", "[A, B, C]", "list");
        assertEquals(expectedMessage, error.getMessage());
    }

    @Test
    public void multipleLevelsDetailedPropertyValueError() {
        PropertyErrorMandatoryValueMissingDetail error = new PropertyErrorMandatoryValueMissingDetail("property",
                "[A, B, C]");
        PropertyErrorReferenceDetail ref1 = new PropertyErrorReferenceDetail("list1", "id1");
        PropertyErrorReferenceDetail ref2 = new PropertyErrorReferenceDetail("list2", "id2");
        PropertyErrorReferenceDetail ref3 = new PropertyErrorReferenceDetail("list3");
        error.addReferencePropertyErrorDetail(ref3);
        error.addReferencePropertyErrorDetail(ref2);
        error.addReferencePropertyErrorDetail(ref1);

        String expectedMessage = "Mandatory property missing: 'property' with value: '[A, B, C]' under list3 under list2 id2 under list1 id1";
        assertEquals(expectedMessage, error.getMessage());
    }
}
