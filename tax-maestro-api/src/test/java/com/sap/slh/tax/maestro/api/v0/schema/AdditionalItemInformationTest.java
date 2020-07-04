package com.sap.slh.tax.maestro.api.v0.schema;

import static org.junit.Assert.assertEquals;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import java.io.IOException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class AdditionalItemInformationTest {
    @Test
    public void serializationAndDeserialization() throws IOException {
        String actualJson = "{\"type\":\"type\",\"information\":\"information\"}";
        AdditionalItemInformation actualObject = new ObjectMapper().readValue(actualJson,
                AdditionalItemInformation.class);

        AdditionalItemInformation expectedObject = AdditionalItemInformation.builder().withType("type")
                .withInformation("information").build();

        assertEquals(expectedObject, actualObject);
        assertEquals(actualObject, new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(expectedObject),
            AdditionalItemInformation.class));
    }

    @Test
    public void objectValidation() {
        AdditionalItemInformation.builder().build().validate();
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(AdditionalItemInformation.class)
            .suppress(nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE, nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS)
            .verify();
    }
}
