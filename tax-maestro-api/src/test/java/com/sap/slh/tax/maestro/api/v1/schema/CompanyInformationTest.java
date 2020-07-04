package com.sap.slh.tax.maestro.api.v1.schema;

import static com.sap.slh.tax.maestro.api.assertion.HasMandatoryAttribute.hasMandatoryItems;
import static com.sap.slh.tax.maestro.api.v1.schema.CompanyInformation.JSONParameter.ASSIGNED_PARTY_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;

import nl.jqno.equalsverifier.EqualsVerifier;

public class CompanyInformationTest {

    private final static String ID = "id";

    private ObjectMapper mapper = new ObjectMapper();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void serializeAndDeserialize() throws IOException {
        CompanyInformation companyInformation = this.getDefaultAssignedParty();
        String serialized = mapper.writeValueAsString(companyInformation);
        CompanyInformation deserialized = mapper.readValue(serialized, CompanyInformation.class);

        assertEquals(companyInformation, deserialized);
    }

    @Test
    public void nullValidation() {
        try {
            CompanyInformation.builder().build().validate();
            fail();
        } catch (InvalidModelException e) {
            assertThat("not null", e.getPropertyErrors(), hasMandatoryItems(ASSIGNED_PARTY_ID));
        }
    }

    @Test
    public void nullFieldsBuilder() {
        CompanyInformation companyInformation = CompanyInformation.builder().withAssignedPartyId(ID).build();

        assertNull(companyInformation.getIsDeferredTaxEnabled());
        assertNull(companyInformation.getAdditionalInformation());
    }

    @Test
    public void testNumberInBooleanDeserialization() throws IOException {
        expectedException.expect(MismatchedInputException.class);
        String json = "{\"isDeferredTaxEnabled\":100}";
        mapper.readValue(json, QuoteDocument.class);
    }

    @Test
    public void validationWithoutErros() {
        CompanyInformation.builder().withAssignedPartyId(ID).build().validate();
        assertTrue(true);
    }

    private CompanyInformation getDefaultAssignedParty() {
        return CompanyInformation.builder().withAssignedPartyId(ID).withIsDeferredTaxEnabled(Boolean.TRUE)
                .withAdditionalInformation(Collections.emptyMap()).build();
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(CompanyInformation.class)
            .suppress(nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE, nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS)
            .verify();
    }
}
