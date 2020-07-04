package com.sap.slh.tax.maestro.api.v1.schema;

import static com.sap.slh.tax.maestro.api.assertion.HasMandatoryAttribute.hasMandatoryItems;
import static com.sap.slh.tax.maestro.api.v1.schema.CostInformation.JSONParameter.AMOUNT;
import static com.sap.slh.tax.maestro.api.v1.schema.CostInformation.JSONParameter.TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class CostInformationTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void serializeAndDeserialize() throws IOException {
        CostInformation costInformation = this.getDefaultCostInformation();
        String serialized = mapper.writeValueAsString(costInformation);
        CostInformation deserialied = mapper.readValue(serialized, CostInformation.class);

        assertEquals(costInformation, deserialied);
    }

    @Test
    public void nullValidation() {
        try {
            CostInformation.builder().build().validate();
            fail();
        } catch (InvalidModelException e) {
            assertThat("not null", e.getPropertyErrors(), hasMandatoryItems(TYPE, AMOUNT));
        }
    }

    @Test
    public void validationWithoutError() {
        CostInformation.builder().withType("Shipping").withAmount(BigDecimal.TEN).build().validate();
        assertTrue(true);
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(CostInformation.class)
            .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
            .verify();
    }

    private CostInformation getDefaultCostInformation() {
        return CostInformation.builder().withType("type").withAmount(BigDecimal.valueOf(10)).build();
    }
}
