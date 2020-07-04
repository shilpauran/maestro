package com.sap.slh.tax.maestro.api.v0.schema;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
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

public class ExemptionDetailTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void serializationAndDeserialization() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"LocationType\":\"CONTRACT_FROM\",\"exemptionCode\":\"exemptionCode\","
                + "\"tariffId\":\"tariffId\",\"taxRateType\":\"taxRateType\",\"taxType\":\"taxType\","
                + "\"region\":\"region\"}";
        ExemptionDetail actualObject = new ObjectMapper().readValue(actualJson, ExemptionDetail.class);

        ExemptionDetail expectedObject = ExemptionDetail.builder().withLocationType(LocationType.CONTRACT_FROM)
                .withExemptionCode("exemptionCode").withTariffId("tariffId").withTaxType("taxType")
                .withTaxRateType("taxRateType").withRegion("region").build();

        assertEquals(expectedObject, actualObject);
        assertEquals(actualObject, new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(expectedObject),
                ExemptionDetail.class));
    }

    @Test
    public void objectValidation() {
        ExemptionDetail.builder().build().validate();
    }

    @Test
    public void validLocationTypeDomain() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = String.format("{\"LocationType\":\"%s\"}", LocationType.CONTRACT_FROM);

        ExemptionDetail actualObject = new ObjectMapper().readValue(actualJson, ExemptionDetail.class);
        assertEquals(actualObject.getLocationType(), LocationType.CONTRACT_FROM);
    }

    @Test
    public void invalidLocationTypeDomain() throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("LocationType");

        String actualJson = String.format("{\"LocationType\":\"%s\"}", "invalid");
        new ObjectMapper().readValue(actualJson, ExemptionDetail.class);
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(ExemptionDetail.class)
            .suppress(nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE, nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS)
            .verify();
    }
}
