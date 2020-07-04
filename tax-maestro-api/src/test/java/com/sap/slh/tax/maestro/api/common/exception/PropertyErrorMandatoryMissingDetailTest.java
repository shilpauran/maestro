package com.sap.slh.tax.maestro.api.common.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PropertyErrorMandatoryMissingDetailTest {

    private String mandatoryPropertyMessageFormat = "Mandatory property missing: '%s'";
    private String mandatoryPropertyDetailedMessageFormat = "Mandatory property missing: '%s' under %s";

    @Test
    public void simplePropertyError() {
        PropertyErrorMandatoryMissingDetail error = new PropertyErrorMandatoryMissingDetail("property");

        String expectedMessage = String.format(mandatoryPropertyMessageFormat, "property");
        assertEquals(expectedMessage, error.getMessage());
    }

    @Test
    public void detailedPropertyError() {
        PropertyErrorMandatoryMissingDetail error = new PropertyErrorMandatoryMissingDetail("property");
        PropertyErrorReferenceDetail ref1 = new PropertyErrorReferenceDetail("list");
        error.addReferencePropertyErrorDetail(ref1);

        String expectedMessage = String.format(mandatoryPropertyDetailedMessageFormat, "property", "list");
        assertEquals(expectedMessage, error.getMessage());
    }

    @Test
    public void multipleLevelsDetailedPropertyError() {
        PropertyErrorMandatoryMissingDetail error = new PropertyErrorMandatoryMissingDetail("property");
        PropertyErrorReferenceDetail ref1 = new PropertyErrorReferenceDetail("list1", "id1");
        PropertyErrorReferenceDetail ref2 = new PropertyErrorReferenceDetail("list2", "id2");
        PropertyErrorReferenceDetail ref3 = new PropertyErrorReferenceDetail("list3");
        error.addReferencePropertyErrorDetail(ref3);
        error.addReferencePropertyErrorDetail(ref2);
        error.addReferencePropertyErrorDetail(ref1);

        String expectedMessage = "Mandatory property missing: 'property' under list3 under list2 id2 under list1 id1";
        assertEquals(expectedMessage, error.getMessage());
    }
}
