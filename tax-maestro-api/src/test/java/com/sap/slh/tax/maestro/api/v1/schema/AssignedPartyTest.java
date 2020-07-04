package com.sap.slh.tax.maestro.api.v1.schema;

import static com.sap.slh.tax.maestro.api.assertion.HasMandatoryAttribute.hasMandatoryItems;
import static com.sap.slh.tax.maestro.api.v1.schema.AssignedParty.JSONParameter.ID;
import static com.sap.slh.tax.maestro.api.v1.schema.AssignedParty.JSONParameter.ROLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.v1.domain.PartyRole;

import nl.jqno.equalsverifier.EqualsVerifier;

public class AssignedPartyTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void serializeAndDeserialize() throws IOException {
        AssignedParty assignedParty = this.getDefaultAssignedParty();
        String serialized = mapper.writeValueAsString(assignedParty);
        AssignedParty deserialied = mapper.readValue(serialized, AssignedParty.class);

        assertEquals(assignedParty, deserialied);
    }

    @Test
    public void nullValidation() {
        try {
            AssignedParty.builder().build().validate();
            fail();
        } catch (InvalidModelException e) {
            assertThat("not null", e.getPropertyErrors(), hasMandatoryItems(ID, ROLE));
        }
    }

    @Test
    public void validationWithoutErros() {
        AssignedParty.builder().withId("id").withRole(PartyRole.SHIP_FROM).build().validate();
        assertTrue(true);
    }

    private AssignedParty getDefaultAssignedParty() {
        return AssignedParty.builder().withId("id").withRole(PartyRole.SHIP_FROM).build();
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(AssignedParty.class)
            .suppress(nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE, nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS)
            .verify();
    }
}
