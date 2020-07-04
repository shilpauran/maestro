package com.sap.slh.tax.maestro.api.common.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PropertyErrorReferenceDetailTest {

    private String referencePropertyMessageFormat = " under %s";
    private String referencePropertyDetailedMessageFormat = " under %s %s";

    @Test
    public void simpleReferencePropertyError() {
        PropertyErrorReferenceDetail error = new PropertyErrorReferenceDetail("property");

        String expectedMessage = String.format(referencePropertyMessageFormat, "property");
        assertEquals(expectedMessage, error.getMessage());
    }

    @Test
    public void detailedReferencePropertyError() {
        PropertyErrorReferenceDetail ref = new PropertyErrorReferenceDetail("property", "id");

        String expectedMessage = String.format(referencePropertyDetailedMessageFormat, "property", "id");
        assertEquals(expectedMessage, ref.getMessage());
    }

    @Test
    public void multipleLevelsDetailedReferencePropertyError() {
        PropertyErrorReferenceDetail ref = new PropertyErrorReferenceDetail("property");
        PropertyErrorReferenceDetail ref1 = new PropertyErrorReferenceDetail("list1", "id1");
        PropertyErrorReferenceDetail ref2 = new PropertyErrorReferenceDetail("list2", "id2");
        PropertyErrorReferenceDetail ref3 = new PropertyErrorReferenceDetail("list3");
        ref.addReferencePropertyErrorDetail(ref3);
        ref.addReferencePropertyErrorDetail(ref2);
        ref.addReferencePropertyErrorDetail(ref1);

        String expectedMessage = " under property under list3 under list2 id2 under list1 id1";
        assertEquals(expectedMessage, ref.getMessage());
    }
}
