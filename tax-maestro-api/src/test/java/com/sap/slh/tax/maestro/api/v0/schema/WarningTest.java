package com.sap.slh.tax.maestro.api.v0.schema;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.sap.slh.tax.maestro.api.v0.schema.Warning;

import java.io.IOException;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class WarningTest {

    @Test
    public void serializationAndDeserialization() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"code\":\"code\",\"description\":\"description\"}";
        Warning actualObject = new ObjectMapper().readValue(actualJson, Warning.class);

        Warning expectedObject = Warning.builder().withCode("code").withDescription("description").build();

        assertEquals(expectedObject, actualObject);
        assertEquals(actualObject,
                new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(expectedObject), Warning.class));
    }

    @Test
    public void objectValidation() {
        Warning.builder().build().validate();
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(Warning.class)
            .suppress(nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE, nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS)
            .verify();
    }
}
