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

public class CostInformationTest {

    @Test
    public void serializationAndDeserialization() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"costType\":\"costType\",\"amount\":\"amount\"}";
        CostInformation actualObject = new ObjectMapper().readValue(actualJson, CostInformation.class);

        CostInformation expectedObject = CostInformation.builder().withCostType("costType").withAmount("amount")
                .build();

        assertEquals(expectedObject, actualObject);
        assertEquals(actualObject, new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(expectedObject),
                CostInformation.class));
    }

    @Test
    public void objectValidation() {
        CostInformation.builder().build().validate();
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(CostInformation.class)
            .suppress(nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE, nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS)
            .verify();
    }
}
