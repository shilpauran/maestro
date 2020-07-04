package com.sap.slh.tax.maestro.api.v0.schema;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.sap.slh.tax.maestro.api.v0.schema.TaxAttribute;

import java.io.IOException;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class TaxAttributeTest {

    @Test
    public void serializationAndDeserialization() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"attributeType\":\"attributeType\",\"attributeValue\":\"attributeValue\"}";
        TaxAttribute actualObject = new ObjectMapper().readValue(actualJson, TaxAttribute.class);

        TaxAttribute expectedObject = TaxAttribute.builder().withAttributeType("attributeType")
                .withAttributeValue("attributeValue").build();

        assertEquals(expectedObject, actualObject);
        assertEquals(actualObject, new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(expectedObject),
                TaxAttribute.class));
    }

    @Test
    public void objectValidation() {
        TaxAttribute.builder().build().validate();
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(TaxAttribute.class)
            .suppress(nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE, nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS)
            .verify();
    }
}
