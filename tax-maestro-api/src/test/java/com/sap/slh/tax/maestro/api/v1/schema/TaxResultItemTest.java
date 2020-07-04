package com.sap.slh.tax.maestro.api.v1.schema;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.sap.slh.tax.maestro.api.v1.domain.DueCategory;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class TaxResultItemTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private TaxResultItem.Builder classUnderTestBuilder = TaxResultItem.builder();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup() {
        objectMapper.disable(MapperFeature.ALLOW_COERCION_OF_SCALARS);
    }

    @Test
    public void serializationAndDeserialization() throws IOException {
        String actualJson = "{\"taxTypeCode\":\"taxTypeCode\"," + "\"taxRate\":11.23,\"taxableBaseAmount\":6.7,"
                + "\"taxAmount\":80,"
                + "\"dueCategoryCode\":\"PAYABLE\",\"isTaxDeferred\":true,\"nonDeductibleTaxRate\":12.34,"
                + "\"deductibleTaxAmount\":50,\"nonDeductibleTaxAmount\":50,"
                + "\"additionalInformation\": {\"aKey\": \"itsValue\"}}";

        TaxResultItem actualObject = objectMapper.readValue(actualJson, TaxResultItem.class);

        Map<String, String> additionalTaxResultAttributes = new HashMap<>();
        additionalTaxResultAttributes.put("aKey", "itsValue");

        TaxResultItem expectedObject = classUnderTestBuilder.withTaxTypeCode("taxTypeCode")
                .withTaxRate(new BigDecimal("11.23")).withTaxableBaseAmount(BigDecimal.valueOf(6.7))

                .withTaxAmount(BigDecimal.valueOf(80)).withDueCategoryCode(DueCategory.PAYABLE).withIsTaxDeferred(true)
                .withNonDeductibleTaxRate(new BigDecimal("12.34")).withDeductibleTaxAmount(BigDecimal.valueOf(50))
                .withNonDeductibleTaxAmount(BigDecimal.valueOf(50))
                .withAdditionalInformation(additionalTaxResultAttributes).build();

        assertEquals(expectedObject, actualObject);
        assertEquals(expectedObject.toString(), actualObject.toString());
        assertEquals(expectedObject.hashCode(), actualObject.hashCode());
        assertEquals(actualObject,
                objectMapper.readValue(new ObjectMapper().writeValueAsString(expectedObject), TaxResultItem.class));
    }

    @Test
    public void objectValidation() {
        classUnderTestBuilder.build().validate();
    }

    @Test
    public void validDueCategoryCodeDomain() throws IOException {
        String actualJson = "{\"taxTypeCode\":\"taxTypeCode\"," + "\"taxRate\":11.23,\"taxableBaseAmount\":6.7,"
                + "\"taxAmount\":80,"
                + "\"dueCategoryCode\":\"PAYABLE\",\"isTaxDeferred\":true,\"nonDeductibleTaxRate\":12.34,"
                + "\"deductibleTaxAmount\":50,\"nonDeductibleTaxAmount\":50,"
                + "\"additionalInformation\": {\"key\": \"aKey\",\"value\": \"itsValue\"}}";
        TaxResultItem actualObject = new ObjectMapper().readValue(actualJson, TaxResultItem.class);

        assertEquals(actualObject.getDueCategoryCode(), DueCategory.PAYABLE);
    }

    @Test
    public void invalidDueCategoryCodeDomain() throws IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("DueCategory");

        String actualJson = "{\"taxTypeCode\":\"taxTypeCode\"," + "\"taxRate\":11.23,\"taxableBaseAmount\":6.7,"
                + "\"taxAmount\":80,"
                + "\"dueCategoryCode\":\"GIVEMETHEMONEY\",\"isTaxDeferred\":true,\"nonDeductibleTaxRate\":12.34,"
                + "\"deductibleTaxAmount\":50,\"nonDeductibleTaxAmount\":50,"
                + "\"additionalInformation\": {\"key\": \"aKey\",\"value\": \"itsValue\"}}";
        new ObjectMapper().readValue(actualJson, TaxResultItem.class);
    }

    @Test
    public void testNumberInBooleanDeserialization() throws IOException {
        thrown.expect(MismatchedInputException.class);

        String json = "{\"isTaxDeferred\":100}";

        objectMapper.readValue(json, TaxResultItem.class);
    }

    @Test
    public void testStringInBooleanDeserialization() throws IOException {
        thrown.expect(MismatchedInputException.class);

        String json = "{\"isTaxDeferred\":\"abc\"}";

        objectMapper.readValue(json, TaxResultItem.class);
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(TaxResultItem.class)
            .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
            .verify();
    }
}
