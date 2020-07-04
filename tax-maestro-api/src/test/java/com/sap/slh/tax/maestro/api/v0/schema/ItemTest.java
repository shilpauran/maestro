package com.sap.slh.tax.maestro.api.v0.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.v0.domain.ItemType;
import com.sap.slh.tax.maestro.api.v0.domain.TaxCategory;

public class ItemTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private String mandatoryPropertyMessageFormat = "Mandatory property missing: '%s'";

    @Test
    public void serializationAndDeserialization() throws IOException {
        String actualJson = "{\"id\":\"id\",\"taxCategory\":\"PRODUCT_TAXES\",\"taxCodeCountry\":\"BR\","
                + "\"taxCodeRegion\":\"taxCodeRegion\",\"taxCode\":\"taxCode\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}";
        Item actualObject = new ObjectMapper().readValue(actualJson, Item.class);

        Item expectedObject = Item.builder().withId("id").withItemCode("itemCode").withQuantity(new BigDecimal(1))
                .withUnitPrice(new BigDecimal(2)).withShippingCost("shippingCost").withItemType(ItemType.M)
                .withExemptionDetails(new ArrayList<ExemptionDetail>()).withCertificateId("1")
                .withItemClassifications(new ArrayList<ItemClassification>())
                .withAdditionalItemInformation(new ArrayList<AdditionalItemInformation>())
                .withCostInformation(new ArrayList<CostInformation>()).withTaxCategory(TaxCategory.PRODUCT_TAXES)
                .withTaxCodeCountry(CountryCode.BR).withTaxCode("taxCode").withTaxCodeRegion("taxCodeRegion").build();

        assertEquals(expectedObject, actualObject);
        assertEquals(actualObject,
                new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(expectedObject), Item.class));
    }

    @Test
    public void objectValidation() {
        try {
            Item.builder().build().validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage0 = String.format(mandatoryPropertyMessageFormat, "id");
            String expectedMessage1 = String.format(mandatoryPropertyMessageFormat, "quantity");
            String expectedMessage2 = String.format(mandatoryPropertyMessageFormat, "unitPrice");

            assertEquals(3, ex.getPropertyErrors().size());
            assertEquals(expectedMessage0, ex.getPropertyErrors().get(0).getMessage());
            assertEquals(expectedMessage1, ex.getPropertyErrors().get(1).getMessage());
            assertEquals(expectedMessage2, ex.getPropertyErrors().get(2).getMessage());
        }
    }

    @Test
    public void validCountryCodeDomain() throws IOException {
        String actualJson = "{\"id\":\"id\",\"taxCategory\":\"PRODUCT_TAXES\",\"taxCodeCountry\":\"BR\","
                + "\"taxCodeRegion\":\"taxCodeRegion\",\"taxCode\":\"taxCode\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}";
        Item actualObject = new ObjectMapper().readValue(actualJson, Item.class);

        assertEquals(actualObject.getTaxCodeCountry(), CountryCode.BR);
    }

    @Test
    public void invalidCountryCountryDomain() throws IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("CountryCode");

        String actualJson = "{\"id\":\"id\",\"taxCategory\":\"PRODUCT_TAXES\",\"taxCodeCountry\":\"INVALID\","
                + "\"taxCodeRegion\":\"taxCodeRegion\",\"taxCode\":\"taxCode\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}";
        new ObjectMapper().readValue(actualJson, Item.class);
    }

    @Test
    public void validTaxCategoryDomain() throws IOException {
        String actualJson = "{\"id\":\"id\",\"taxCategory\":\"PRODUCT_TAXES\",\"taxCodeCountry\":\"BR\","
                + "\"taxCodeRegion\":\"taxCodeRegion\",\"taxCode\":\"taxCode\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}";
        Item actualObject = new ObjectMapper().readValue(actualJson, Item.class);

        assertEquals(actualObject.getTaxCategory(), TaxCategory.PRODUCT_TAXES);
    }

    @Test
    public void invalidTaxCategoryDomain() throws IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("TaxCategory");

        String actualJson = "{\"id\":\"id\",\"taxCategory\":\"INVALID\",\"taxCodeCountry\":\"BR\","
                + "\"taxCodeRegion\":\"taxCodeRegion\",\"taxCode\":\"taxCode\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}";
        new ObjectMapper().readValue(actualJson, Item.class);
    }

    @Test
    public void validateMissingId() throws IOException {
        String actualJson = "{\"taxCategory\":\"PRODUCT_TAXES\",\"taxCodeCountry\":\"BR\","
                + "\"taxCodeRegion\":\"taxCodeRegion\",\"taxCode\":\"taxCode\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}";

        try {
            new ObjectMapper().readValue(actualJson, Item.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "id");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateNullId() throws IOException {
        String actualJson = "{\"id\":null,\"taxCategory\":\"PRODUCT_TAXES\",\"taxCodeCountry\":\"BR\","
                + "\"taxCodeRegion\":\"taxCodeRegion\",\"taxCode\":\"taxCode\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}";

        try {
            new ObjectMapper().readValue(actualJson, Item.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "id");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateEmptyId() throws IOException {
        String actualJson = "{\"id\":\"\",\"taxCategory\":\"PRODUCT_TAXES\",\"taxCodeCountry\":\"BR\","
                + "\"taxCodeRegion\":\"taxCodeRegion\",\"taxCode\":\"taxCode\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}";

        try {
            new ObjectMapper().readValue(actualJson, Item.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "id");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateMissingQuantity() throws IOException {

        String actualJson = "{\"id\":\"id\",\"taxCategory\":\"PRODUCT_TAXES\",\"taxCodeCountry\":\"BR\","
                + "\"taxCodeRegion\":\"taxCodeRegion\",\"taxCode\":\"taxCode\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"unitPrice\":\"2\",\"certificateId\":\"1\",\"shippingCost\":\"shippingCost\","
                + "\"exemptionDetails\":[],\"itemClassifications\":[],\"additionalItemInformation\":[],"
                + "\"costInformation\":[]}";

        try {
            new ObjectMapper().readValue(actualJson, Item.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "quantity");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateNullQuantity() throws IOException {
        String actualJson = "{\"id\":\"id\",\"taxCategory\":\"PRODUCT_TAXES\",\"taxCodeCountry\":\"BR\","
                + "\"taxCodeRegion\":\"taxCodeRegion\",\"taxCode\":\"taxCode\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":null,\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}";

        try {
            new ObjectMapper().readValue(actualJson, Item.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "quantity");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateEmptyQuantity() throws IOException {
        String actualJson = "{\"id\":\"id\",\"taxCategory\":\"PRODUCT_TAXES\",\"taxCodeCountry\":\"BR\","
                + "\"taxCodeRegion\":\"taxCodeRegion\",\"taxCode\":\"taxCode\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"\",\"unitPrice\":\"2\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}";

        try {
            new ObjectMapper().readValue(actualJson, Item.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "quantity");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateMissingUnitPrice() throws IOException {
        String actualJson = "{\"id\":\"id\",\"taxCategory\":\"PRODUCT_TAXES\",\"taxCodeCountry\":\"BR\","
                + "\"taxCodeRegion\":\"taxCodeRegion\",\"taxCode\":\"taxCode\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"certificateId\":\"1\",\"shippingCost\":\"shippingCost\","
                + "\"exemptionDetails\":[],\"itemClassifications\":[],\"additionalItemInformation\":[],"
                + "\"costInformation\":[]}";

        try {
            new ObjectMapper().readValue(actualJson, Item.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "unitPrice");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateNullUnitPrice() throws IOException {
        String actualJson = "{\"id\":\"id\",\"taxCategory\":\"PRODUCT_TAXES\",\"taxCodeCountry\":\"BR\","
                + "\"taxCodeRegion\":\"taxCodeRegion\",\"taxCode\":\"taxCode\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":null,\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}";

        try {
            new ObjectMapper().readValue(actualJson, Item.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "unitPrice");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateEmptyUnitPrice() throws IOException {
        String actualJson = "{\"id\":\"id\",\"taxCategory\":\"PRODUCT_TAXES\",\"taxCodeCountry\":\"BR\","
                + "\"taxCodeRegion\":\"taxCodeRegion\",\"taxCode\":\"taxCode\",\"itemCode\":\"itemCode\","
                + "\"itemType\":\"M\",\"quantity\":\"1\",\"unitPrice\":\"\",\"certificateId\":\"1\","
                + "\"shippingCost\":\"shippingCost\",\"exemptionDetails\":[],\"itemClassifications\":[],"
                + "\"additionalItemInformation\":[],\"costInformation\":[]}";

        try {
            new ObjectMapper().readValue(actualJson, Item.class).validate();
            fail("Expected Exception InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "unitPrice");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(Item.class)
            .suppress(nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE, nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS)
            .verify();
    }
}
