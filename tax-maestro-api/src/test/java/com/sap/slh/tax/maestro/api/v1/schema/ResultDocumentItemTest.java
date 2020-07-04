package com.sap.slh.tax.maestro.api.v1.schema;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class ResultDocumentItemTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ResultDocumentItem.Builder classUnderTestBuilder = ResultDocumentItem.builder();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void serializationAndDeserialization()
            throws JsonParseException, JsonMappingException, IOException, ParseException {
        String actualJson = "{\"id\":\"id\",\"countryRegionCode\":\"US\","
                + "\"subdivisionCode\":\"BC\",\"taxEventCode\":\"taxEventCode\","
                + "\"taxEventLegalPhrase\":\"taxEventLegalPhrase\",\"taxes\":[],"
                + "\"additionalInformation\": {\"aKey\": \"itsValue\"}}";
        ResultDocumentItem actualObject = objectMapper.readValue(actualJson, ResultDocumentItem.class);

        List<TaxResultItem> taxResults = new ArrayList<TaxResultItem>();
        Map<String, String> additionalInformation = new HashMap<String, String>();
        additionalInformation.put("aKey", "itsValue");

        ResultDocumentItem expectedObject = classUnderTestBuilder.withId("id").withCountryRegionCode(CountryCode.US)
                .withSubdivisionCode("BC").withTaxEventCode("taxEventCode").withTaxEventLegalPhrase("taxEventLegalPhrase")
                .withTaxes(taxResults).withAdditionalInformation(additionalInformation).build();

        assertEquals(expectedObject, actualObject);
        assertEquals(expectedObject.toString(), actualObject.toString());
        assertEquals(expectedObject.hashCode(), actualObject.hashCode());
        assertEquals(actualObject, objectMapper.readValue(new ObjectMapper().writeValueAsString(expectedObject),
                ResultDocumentItem.class));
    }

    @Test
    public void objectValidation() {
        classUnderTestBuilder.build().validate();
    }

    @Test
    public void validCountryRegionDomain() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"countryRegionCode\":\"US\"," + "\"taxEventCode\":\"taxEventCode\","
                + "\"taxEventLegalPhrase\":\"taxEventLegalPhrase\",\"taxes\":[],"
                + "\"additionalInformation\": {\"aKey\": \"itsValue\"}}";
        ResultDocumentItem actualObject = new ObjectMapper().readValue(actualJson, ResultDocumentItem.class);

        assertEquals(actualObject.getCountryRegionCode(), CountryCode.US);
    }

    @Test
    public void invalidCountryRegionDomain() throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("CountryCode");

        String actualJson = "{\"id\":\"id\",\"countryRegionCode\":\"EUA\","
                + "\"taxEventCode\":\"taxEventCode\",\"taxEventDescription\":\"taxEventDescription\","
                + "\"taxEventLegalPhrase\":\"taxEventLegalPhrase\",\"taxes\":[],"
                + "\"additionalInformation\": {\"aKey\": \"itsValue\"}}";
        new ObjectMapper().readValue(actualJson, ResultDocumentItem.class);
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(ResultDocumentItem.class)
            .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
            .verify();
    }
}
