package com.sap.slh.tax.maestro.api.v1.schema;

import static com.sap.slh.tax.maestro.api.assertion.HasMandatoryAttribute.hasMandatoryItems;
import static com.sap.slh.tax.maestro.api.v1.schema.ProductTaxClassification.JSONParameter.COUNTRY_REGION_CODE;
import static com.sap.slh.tax.maestro.api.v1.schema.ProductTaxClassification.JSONParameter.EXEMPTION_REASON_CODE;
import static com.sap.slh.tax.maestro.api.v1.schema.ProductTaxClassification.JSONParameter.TAX_RATE_TYPE_CODE;
import static com.sap.slh.tax.maestro.api.v1.schema.ProductTaxClassification.JSONParameter.TAX_TYPE_CODE;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class ProductTaxClassificationTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        mapper.disable(MapperFeature.ALLOW_COERCION_OF_SCALARS);
    }

    @Test
    public void nullBuilder() {
        ProductTaxClassification taxClassification = ProductTaxClassification.builder().build();
        assertNull(taxClassification.getCountryRegionCode());
        assertNull(taxClassification.getSubdivisionCode());
        assertNull(taxClassification.getTaxTypeCode());
        assertNull(taxClassification.getTaxRateTypeCode());
        assertNull(taxClassification.getExemptionReasonCode());
        assertNull(taxClassification.getIsServicePointTaxable());
        assertNull(taxClassification.getIsSoldElectronically());
    }

    @Test
    public void serializeAndDeserialize() throws IOException {
        ProductTaxClassification productTaxClassification = this.getDefaultProductTaxClassification();
        String serialized = mapper.writeValueAsString(productTaxClassification);
        ProductTaxClassification deserialized = mapper.readValue(serialized, ProductTaxClassification.class);

        assertEquals(productTaxClassification, deserialized);
    }

    @Test
    public void validateNullFields() {
        try {
            ProductTaxClassification.builder().build().validate();
            fail();
        } catch (InvalidModelException e) {
            assertThat(e.getPropertyErrors(), hasMandatoryItems(COUNTRY_REGION_CODE, TAX_TYPE_CODE));
        }
    }

    @Test
    public void validateMissingTaxRateButProvidingExemption() {
        try {
            ProductTaxClassification.builder().withExemptionReasonCode("lala").build().validate();
            fail();
        } catch (InvalidModelException e) {
            assertThat(e.getPropertyErrors(), not(hasMandatoryItems(EXEMPTION_REASON_CODE, TAX_RATE_TYPE_CODE)));
        }
    }

    @Test
    public void validateMissingExemptionButProvidingTaxRateCode() {
        try {
            ProductTaxClassification.builder().withTaxRateTypeCode("lala").build().validate();
            fail();
        } catch (InvalidModelException e) {
            assertThat(e.getPropertyErrors(), not(hasMandatoryItems(EXEMPTION_REASON_CODE, TAX_RATE_TYPE_CODE)));
        }
    }

    @Test
    public void validTaxClassification() {
        ProductTaxClassification.builder().withCountryRegionCode(CountryCode.BR).withTaxTypeCode("VAT")
                .withTaxRateTypeCode("10").build().validate();
    }

    @Test
    public void testNumberBooleanDeserialization() throws IOException {
        String json = "{\"isSoldElectronically\": 100}";

        expectedException.expect(MismatchedInputException.class);
        mapper.readValue(json, ProductTaxClassification.class);
    }

    @Test
    public void testStringBooleanDeserialization() throws IOException {
        String json = "{\"isServicePointTaxable\": \"abc\" }";

        expectedException.expect(MismatchedInputException.class);
        mapper.readValue(json, ProductTaxClassification.class);
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(ProductTaxClassification.class)
            .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
            .verify();
    }

    private ProductTaxClassification getDefaultProductTaxClassification() {
        return ProductTaxClassification.builder().withCountryRegionCode(CountryCode.AD)
                .withSubdivisionCode("subdivisionCode").withTaxTypeCode("taxTypeCode")
                .withTaxRateTypeCode("taxRateTypeCode").withExemptionReasonCode("taxExemptionReasonCode")
                .withIsSoldElectronically(false).withIsServicePointTaxable(false).build();
    }
}
