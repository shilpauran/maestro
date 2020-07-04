package com.sap.slh.tax.maestro.api.common.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PropertyErrorValueOutOfBoundsDetailTest {

    private String mandatoryPropertyMessageFormat = "Value for property '%s' out of specified interval [%s, %s]";
    private String mandatoryPropertyDetailedMessageFormat = "Value for property '%s' out of specified interval [%s, %s] under %s";

    @Test
    public void simplePropertyValueError() {
        PropertyErrorValueOutOfBoundsDetail error = new PropertyErrorValueOutOfBoundsDetail("property", "0", "100");

        String expectedMessage = String.format(mandatoryPropertyMessageFormat, "property", "0", "100");
        assertEquals(expectedMessage, error.getMessage());
    }

    @Test
    public void detailedPropertyValueError() {
        PropertyErrorValueOutOfBoundsDetail error = new PropertyErrorValueOutOfBoundsDetail("property", "0", "100");
        PropertyErrorReferenceDetail ref1 = new PropertyErrorReferenceDetail("list");
        error.addReferencePropertyErrorDetail(ref1);

        String expectedMessage = String.format(mandatoryPropertyDetailedMessageFormat, "property", "0", "100", "list");
        assertEquals(expectedMessage, error.getMessage());
    }

    @Test
    public void multipleLevelsDetailedPropertyValueError() {
        PropertyErrorValueOutOfBoundsDetail error = new PropertyErrorValueOutOfBoundsDetail("property", "0", "100");
        PropertyErrorReferenceDetail ref1 = new PropertyErrorReferenceDetail("list1", "id1");
        PropertyErrorReferenceDetail ref2 = new PropertyErrorReferenceDetail("list2", "id2");
        PropertyErrorReferenceDetail ref3 = new PropertyErrorReferenceDetail("list3");
        error.addReferencePropertyErrorDetail(ref3);
        error.addReferencePropertyErrorDetail(ref2);
        error.addReferencePropertyErrorDetail(ref1);

        String expectedMessage = "Value for property 'property' out of specified interval [0, 100] under list3 under list2 id2 under list1 id1";
        assertEquals(expectedMessage, error.getMessage());
    }
}
