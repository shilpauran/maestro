package com.sap.slh.tax.maestro.api.v0.schema;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.sap.slh.tax.maestro.api.v0.domain.LocationType;

import java.io.IOException;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TaxRegistrationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void serializationAndDeserialization() throws IOException {
        String actualJson = "{\"locationType\":\"CONTRACT_FROM\",\"taxNumberTypeCode\":\"taxNumberTypeCode\","
                + "\"taxNumber\":\"taxNumber\"}";
        TaxRegistration actualObject = new ObjectMapper().readValue(actualJson, TaxRegistration.class);

        TaxRegistration expectedObject = TaxRegistration.builder().withLocationType(LocationType.CONTRACT_FROM)
                .withTaxNumberTypeCode("taxNumberTypeCode").withTaxNumber("taxNumber").build();

        assertEquals(expectedObject, actualObject);
        assertEquals(actualObject, new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(expectedObject),
                TaxRegistration.class));
    }

    @Test
    public void objectValidation() {
        TaxRegistration.builder().build().validate();
    }

    @Test
    public void validLocationTypeDomain() throws IOException {
        String actualJson = String.format("{\"locationType\":\"%s\"}", LocationType.CONTRACT_FROM);

        TaxRegistration actualObject = new ObjectMapper().readValue(actualJson, TaxRegistration.class);
        assertEquals(actualObject.getLocationType(), LocationType.CONTRACT_FROM);
    }

    @Test
    public void invalidLocationTypeDomain() throws IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("LocationType");

        String actualJson = String.format("{\"locationType\":\"%s\"}", "invalid");
        new ObjectMapper().readValue(actualJson, TaxRegistration.class);
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(TaxRegistration.class)
            .suppress(nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE, nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS)
            .verify();
    }
}
