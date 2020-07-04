package com.sap.slh.tax.maestro.api.v0.schema;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.sap.slh.tax.maestro.api.v0.domain.LocationType;
import com.sap.slh.tax.maestro.api.v0.schema.Party;
import com.sap.slh.tax.maestro.api.v0.schema.TaxRegistration;

import java.io.IOException;
import java.util.ArrayList;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PartyTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void serializationAndDeserialization() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"id\":\"id\",\"role\":\"CONTRACT_FROM\",\"taxRegistration\":[],"
                + "\"removedTaxRegistrations\":[]}";
        Party actualObject = new ObjectMapper().readValue(actualJson, Party.class);

        Party expectedObject = Party.builder().withId("id").withRole(LocationType.CONTRACT_FROM)
                .withTaxRegistration(new ArrayList<TaxRegistration>())
                .withRemovedTaxRegistrations(new ArrayList<TaxRegistration>()).build();

        assertEquals(expectedObject, actualObject);
        assertEquals(actualObject,
                new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(expectedObject), Party.class));
    }

    @Test
    public void objectValidation() {
        Party.builder().build().validate();
    }

    @Test
    public void validLocationTypeDomain() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = String.format("{\"role\":\"%s\"}", LocationType.CONTRACT_FROM);

        Party actualObject = new ObjectMapper().readValue(actualJson, Party.class);
        assertEquals(actualObject.getRole(), LocationType.CONTRACT_FROM);
    }

    @Test
    public void invalidLocationTypeDomain() throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("LocationType");

        String actualJson = String.format("{\"role\":\"%s\"}", "invalid");
        new ObjectMapper().readValue(actualJson, Party.class);
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(Party.class)
            .suppress(nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE, nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS)
            .verify();
    }
}
