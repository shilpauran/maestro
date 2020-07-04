package com.sap.slh.tax.maestro.api.v0.schema;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;

import java.io.IOException;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class ItemClassificationTest {

    @Test
    public void serializationAndDeserialization() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"itemStandardClassificationSystemCode\":\"itemStandardClassificationSystemCode\","
                + "\"itemStandardClassificationCode\":\"itemStandardClassificationCode\"}";
        ItemClassification actualObject = new ObjectMapper().readValue(actualJson, ItemClassification.class);

        ItemClassification expectedObject = ItemClassification.builder()
                .withItemStandardClassificationSystemCode("itemStandardClassificationSystemCode")
                .withItemStandardClassificationCode("itemStandardClassificationCode").build();

        assertEquals(expectedObject, actualObject);
        assertEquals(actualObject, new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(expectedObject),
                ItemClassification.class));
    }

    @Test
    public void objectValidation() {
        ItemClassification.builder().build().validate();
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(ItemClassification.class)
            .suppress(nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE, nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS)
            .verify();
    }
}
