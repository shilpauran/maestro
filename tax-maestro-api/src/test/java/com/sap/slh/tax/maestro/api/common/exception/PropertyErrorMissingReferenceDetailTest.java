package com.sap.slh.tax.maestro.api.common.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PropertyErrorMissingReferenceDetailTest {
    private String mandatoryPropertyMessageFormat = "Missing reference details for property '%s' with id '%s'";
    private String mandatoryPropertyDetailedMessageFormat = "Missing reference details for property '%s' with id '%s' under %s";

    @Test
    public void simplePropertyValueError() {
        PropertyErrorMissingReferenceDetail error = new PropertyErrorMissingReferenceDetail("property", "1");

        String expectedMessage = String.format(mandatoryPropertyMessageFormat, "property", "1");
        assertEquals(expectedMessage, error.getMessage());
    }

    @Test
    public void detailedPropertyValueError() {
        PropertyErrorMissingReferenceDetail error = new PropertyErrorMissingReferenceDetail("property", "1");
        PropertyErrorReferenceDetail ref1 = new PropertyErrorReferenceDetail("list");
        error.addReferencePropertyErrorDetail(ref1);

        String expectedMessage = String.format(mandatoryPropertyDetailedMessageFormat, "property", "1", "list");
        assertEquals(expectedMessage, error.getMessage());
    }

    @Test
    public void multipleLevelsDetailedPropertyValueError() {
        PropertyErrorMissingReferenceDetail error = new PropertyErrorMissingReferenceDetail("property", "1");
        PropertyErrorReferenceDetail ref1 = new PropertyErrorReferenceDetail("list1", "id1");
        PropertyErrorReferenceDetail ref2 = new PropertyErrorReferenceDetail("list2", "id2");
        PropertyErrorReferenceDetail ref3 = new PropertyErrorReferenceDetail("list3");
        error.addReferencePropertyErrorDetail(ref3);
        error.addReferencePropertyErrorDetail(ref2);
        error.addReferencePropertyErrorDetail(ref1);

        String expectedMessage = "Missing reference details for property 'property' with id '1' under list3 under list2 id2 under list1 id1";
        assertEquals(expectedMessage, error.getMessage());
    }
}
