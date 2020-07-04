package com.sap.slh.tax.maestro.api.v0.schema;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.v0.schema.TaxLine;
import com.sap.slh.tax.maestro.api.v0.schema.TaxValue;

import java.io.IOException;
import java.util.ArrayList;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TaxLineTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void serializationAndDeserialization() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"country\":\"BR\",\"totalTax\":\"totalTax\",\"taxcode\":\"taxCode\","
                + "\"taxCodeDescription\":\"taxCodeDescription\",\"taxCodeLegalPhrase\":\"taxCodeLegalPhrase\","
                + "\"totalRate\":\"totalRate\",\"totalWithholdingTax\":\"totalWithholdingTax\","
                + "\"totalWithholdingTaxRate\":\"totalWithholdingTaxRate\","
                + "\"withholdingTaxCode\":\"withholdingTaxCode\",\"taxValues\":[]}";
        TaxLine actualObject = new ObjectMapper().readValue(actualJson, TaxLine.class);

        TaxLine expectedObject = TaxLine.builder().withId("id").withCountry(CountryCode.BR).withTotalTax("totalTax")
                .withTaxCode("taxCode").withTaxCodeDescription("taxCodeDescription")
                .withTaxCodeLegalPhrase("taxCodeLegalPhrase").withTotalRate("totalRate")
                .withTotalWithholdingTax("totalWithholdingTax").withWithholdingTaxCode("withholdingTaxCode")
                .withTotalWithholdingTaxRate("totalWithholdingTaxRate").withTaxValues(new ArrayList<TaxValue>())
                .build();

        assertEquals(expectedObject, actualObject);
        assertEquals(actualObject,
                new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(expectedObject), TaxLine.class));
    }

    @Test
    public void objectValidation() {
        TaxLine.builder().build().validate();
    }

    @Test
    public void validCountryCodeDomain() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = String.format("{\"country\":\"%s\"}", CountryCode.BR);

        TaxLine actualObject = new ObjectMapper().readValue(actualJson, TaxLine.class);
        assertEquals(actualObject.getCountry(), CountryCode.BR);
    }

    @Test
    public void invalidCountryCodeDomain() throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("CountryCode");

        String actualJson = String.format("{\"country\":\"%s\"}", "invalid");
        new ObjectMapper().readValue(actualJson, TaxLine.class);
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(TaxLine.class)
            .suppress(nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE, nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS)
            .verify();
    }
}
