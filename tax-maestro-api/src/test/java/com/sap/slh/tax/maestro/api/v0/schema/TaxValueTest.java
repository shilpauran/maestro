package com.sap.slh.tax.maestro.api.v0.schema;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.sap.slh.tax.maestro.api.v0.domain.DueCategory;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TaxValueTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
        mapper.disable(MapperFeature.ALLOW_COERCION_OF_SCALARS);
    }

    @Test
    public void serializationAndDeserialization() throws IOException {
        String actualJson = "{\"level\":\"1\",\"taxTypeCode\":\"taxTypeCode\",\"name\":\"name\",\"rate\":\"rate\","
                + "\"taxable\":\"taxable\",\"exemptedBasePercent\":\"exemptedBasePercent\","
                + "\"exemptedBaseAmount\":\"exemptedBaseAmount\",\"otherBaseAmount\":\"otherBaseAmount\","
                + "\"value\":\"value\",\"nonDeductibleTaxRate\":\"nonDeductibleTaxRate\","
                + "\"nonDeductibleTaxAmount\":\"nonDeductibleTaxAmount\","
                + "\"deductibleTaxAmount\":\"deductibleTaxAmount\",\"taxAttributes\":[],"
                + "\"jurisdiction\":\"jurisdiction\",\"jurisdictionCode\":\"jurisdictionCode\",\"dueCategory\":\"P\","
                + "\"isTaxDeferred\":true,\"withholdingRelevant\":false}";
        TaxValue actualObject = new ObjectMapper().readValue(actualJson, TaxValue.class);

        TaxValue expectedObject = TaxValue.builder().withLevel("1").withRate("rate").withTaxable("taxable")
                .withName("name").withValue("value").withNonDeductibleTaxRate("nonDeductibleTaxRate")
                .withNonDeductibleTaxAmount("nonDeductibleTaxAmount").withDeductibleTaxAmount("deductibleTaxAmount")
                .withExemptedBasePercent("exemptedBasePercent").withExemptedBaseAmount("exemptedBaseAmount")
                .withOtherBaseAmount("otherBaseAmount").withTaxTypeCode("taxTypeCode")
                .withTaxAttributes(new ArrayList<>()).withJurisdiction("jurisdiction")
                .withJurisdictionCode("jurisdictionCode").withDueCategory(DueCategory.P).withIsTaxDeferred(true)
                .withWithholdingRelevant(false).build();

        assertEquals(expectedObject, actualObject);
        assertEquals(actualObject,
                new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(expectedObject), TaxValue.class));
    }

    @Test
    public void objectValidation() {
        TaxValue.builder().build().validate();
    }

    @Test
    public void validDueCategoryDomain() throws IOException {
        String actualJson = "{\"level\":\"1\",\"taxTypeCode\":\"taxTypeCode\",\"name\":\"name\",\"rate\":\"rate\","
                + "\"taxable\":\"taxable\",\"exemptedBasePercent\":\"exemptedBasePercent\","
                + "\"exemptedBaseAmount\":\"exemptedBaseAmount\",\"otherBaseAmount\":\"otherBaseAmount\","
                + "\"value\":\"value\",\"nonDeductibleTaxRate\":\"nonDeductibleTaxRate\","
                + "\"nonDeductibleTaxAmount\":\"nonDeductibleTaxAmount\","
                + "\"deductibleTaxAmount\":\"deductibleTaxAmount\",\"taxAttributes\":[],"
                + "\"jurisdiction\":\"jurisdiction\",\"jurisdictionCode\":\"jurisdictionCode\",\"dueCategory\":\"P\","
                + "\"isTaxDeferred\":true,\"withholdingRelevant\":false}";
        TaxValue actualObject = new ObjectMapper().readValue(actualJson, TaxValue.class);

        assertEquals(actualObject.getDueCategory(), DueCategory.P);
    }

    @Test
    public void invalidDueCategoryDomain() throws IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("DueCategory");

        String actualJson = "{\"level\":\"1\",\"taxTypeCode\":\"taxTypeCode\",\"name\":\"name\",\"rate\":\"rate\","
                + "\"taxable\":\"taxable\",\"exemptedBasePercent\":\"exemptedBasePercent\","
                + "\"exemptedBaseAmount\":\"exemptedBaseAmount\",\"otherBaseAmount\":\"otherBaseAmount\","
                + "\"value\":\"value\",\"nonDeductibleTaxRate\":\"nonDeductibleTaxRate\","
                + "\"nonDeductibleTaxAmount\":\"nonDeductibleTaxAmount\","
                + "\"deductibleTaxAmount\":\"deductibleTaxAmount\",\"taxAttributes\":[],"
                + "\"jurisdiction\":\"jurisdiction\",\"jurisdictionCode\":\"jurisdictionCode\","
                + "\"dueCategory\":\"X\",\"isTaxDeferred\":true,\"withholdingRelevant\":false}";
        new ObjectMapper().readValue(actualJson, TaxValue.class);
    }

    @Test
    public void ignoreIdOnDeserialization() throws IOException {
        String actualJson = "{\"id\":\"1\",\"level\":\"1\",\"taxTypeCode\":\"taxTypeCode\",\"name\":\"name\","
                + "\"rate\":\"rate\",\"taxable\":\"taxable\",\"exemptedBasePercent\":\"exemptedBasePercent\","
                + "\"exemptedBaseAmount\":\"exemptedBaseAmount\",\"otherBaseAmount\":\"otherBaseAmount\","
                + "\"value\":\"value\",\"nonDeductibleTaxRate\":\"nonDeductibleTaxRate\","
                + "\"nonDeductibleTaxAmount\":\"nonDeductibleTaxAmount\","
                + "\"deductibleTaxAmount\":\"deductibleTaxAmount\",\"taxAttributes\":[],"
                + "\"jurisdiction\":\"jurisdiction\",\"jurisdictionCode\":\"jurisdictionCode\",\"dueCategory\":\"P\","
                + "\"isTaxDeferred\":true,\"withholdingRelevant\":false}";
        TaxValue actualObject = new ObjectMapper().readValue(actualJson, TaxValue.class);

        assertNull(actualObject.getId());
    }

    @Test
    public void ignoreIdOnSerialization() throws IOException {
        TaxValue actualObject = TaxValue.builder()
                .withId("1")
                .withRate("rate")
                .build();

        String actualJson = new ObjectMapper().writeValueAsString(actualObject);

        assertEquals(actualJson, "{\"rate\":\"rate\"}");
    }

    @Test
    public void testNumberInBooleanDeserialization() throws IOException {
        thrown.expect(MismatchedInputException.class);
        String actualJson = "{\"isTaxDeferred\":100}";
        mapper.readValue(actualJson, TaxValue.class);

    }

    @Test
    public void testStringInBooleanDeserialization() throws IOException {
        thrown.expect(MismatchedInputException.class);
        String actualJson = "{\"withholdingRelevant\":\"abc\"}";
        mapper.readValue(actualJson, TaxValue.class);

    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(TaxValue.class)
            .suppress(nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE, nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS)
            .verify();
    }
}
