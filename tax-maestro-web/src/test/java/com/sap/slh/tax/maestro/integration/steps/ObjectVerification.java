package com.sap.slh.tax.maestro.integration.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.slh.tax.attributes.determination.model.request.Item;
import com.sap.slh.tax.attributes.determination.model.request.PartyDetail;
import com.sap.slh.tax.attributes.determination.model.request.Product;
import com.sap.slh.tax.attributes.determination.model.request.StandardClassification;
import com.sap.slh.tax.attributes.determination.model.request.TaxAttributesDeterminationRequest;
import com.sap.slh.tax.attributes.determination.model.request.TaxClassification;

public class ObjectVerification {

    private ObjectVerification() {
    }

    public static void verifyEqualsString(Object expected, Object actual) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            String expectedBody = mapper.writeValueAsString(expected);
            String actualBody = mapper.writeValueAsString(actual);
            assertEquals(expectedBody, actualBody);
        } catch (JsonProcessingException e) {
            // JSON Conversion error
            assertTrue(false);
        }
    }

    public static void verifyEquals(TaxAttributesDeterminationRequest expected,
            TaxAttributesDeterminationRequest actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getTransactionTypeCode(), actual.getTransactionTypeCode());
        assertEquals(expected.getIsTransactionWithinTaxReportingGroup(),
                actual.getIsTransactionWithinTaxReportingGroup());
        assertEquals(expected.getIsCompanyDeferredTaxEnabled(), actual.getIsCompanyDeferredTaxEnabled());
        assertEquals(expected.getIsTraceEnabled(), actual.getIsTraceEnabled());
        verifyEqualsItem(expected.getItems(), actual.getItems());
        verifyEqualsProduct(expected.getProducts(), actual.getProducts());
    }

    private static void verifyEqualsItem(List<Item> expectedItem, List<Item> actualItem) {
        assertEquals(expectedItem.size(), actualItem.size());
        for (int i = 0; i < expectedItem.size(); i++) {
            Item expected = expectedItem.get(i);
            Item actual = actualItem.get(i);

            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getAssignedProductId(), actual.getAssignedProductId());
            verifyParties(expected.getAssignedParties(), actual.getAssignedParties());
        }
    }

    private static void verifyParties(List<PartyDetail> expectedAssignedParties,
            List<PartyDetail> actualAssignedParties) {
        assertEquals(expectedAssignedParties.size(), actualAssignedParties.size());
        for (int i = 0; i < expectedAssignedParties.size(); i++) {
            PartyDetail expected = expectedAssignedParties.get(i);
            PartyDetail actual = actualAssignedParties.get(i);

            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getRole(), actual.getRole());
        }
    }

    public static void verifyEqualsProduct(List<Product> expectedProduct, List<Product> actualProduct) {
        assertEquals(expectedProduct.size(), actualProduct.size());
        for (int i = 0; i < expectedProduct.size(); i++) {
            Product expected = expectedProduct.get(i);
            Product actual = actualProduct.get(i);

            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getTypeCode(), actual.getTypeCode());
            verifyEqualsTaxClassification(expected.getTaxClassifications(), actual.getTaxClassifications());
            verifyEqualsStandardClassification(expected.getStandardClassifications(),
                    actual.getStandardClassifications());
        }
    }

    private static void verifyEqualsTaxClassification(List<TaxClassification> expectedTaxClassification,
            List<TaxClassification> actualTaxClassification) {
        assertEquals(expectedTaxClassification.size(), actualTaxClassification.size());
        for (int i = 0; i < expectedTaxClassification.size(); i++) {
            TaxClassification expected = expectedTaxClassification.get(i);
            TaxClassification actual = actualTaxClassification.get(i);

            assertEquals(expected.getCountryRegionCode(), actual.getCountryRegionCode());
            assertEquals(expected.getSubdivisionCode(), actual.getSubdivisionCode());
            assertEquals(expected.getTaxTypeCode(), actual.getTaxTypeCode());
            assertEquals(expected.getTaxRateTypeCode(), actual.getTaxRateTypeCode());
            assertEquals(expected.getExemptionReasonCode(), actual.getExemptionReasonCode());
            assertEquals(expected.getIsSoldElectronically(), actual.getIsSoldElectronically());
            assertEquals(expected.getIsServicePointTaxable(), actual.getIsServicePointTaxable());
        }
    }

    private static void verifyEqualsStandardClassification(List<StandardClassification> expectedStandardClassification,
            List<StandardClassification> actualStandardClassification) {
        assertEquals(expectedStandardClassification.size(), actualStandardClassification.size());
        for (int i = 0; i < expectedStandardClassification.size(); i++) {
            StandardClassification expected = expectedStandardClassification.get(i);
            StandardClassification actual = actualStandardClassification.get(i);

            assertEquals(expected.getClassificationSystem(), actual.getClassificationSystem());
            assertEquals(expected.getProductCode(), actual.getProductCode());
        }
    }
}
