package com.sap.slh.tax.maestro.api.v1.schema;

import static com.sap.slh.tax.maestro.api.assertion.HasMandatoryAttribute.hasMandatoryItems;
import static com.sap.slh.tax.maestro.api.v1.schema.PartyTaxRegistration.JSONParameter.TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class PartyTaxRegistrationTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void nullFields() {
        PartyTaxRegistration partyTaxRegistration = PartyTaxRegistration.builder().build();

        assertNull(partyTaxRegistration.getCountryRegionCode());
        assertNull(partyTaxRegistration.getSubdivisionCode());
        assertNull(partyTaxRegistration.getType());
        assertNull(partyTaxRegistration.getNumber());
    }

    @Test
    public void builder() {
        String subDivision = "3";
        String type = "4";
        String number = "5";

        PartyTaxRegistration taxClassification = PartyTaxRegistration.builder().withCountryRegionCode(CountryCode.BR)
                .withSubdivisionCode(subDivision).withType(type).withNumber(number).build();

        assertEquals(taxClassification.getCountryRegionCode(), CountryCode.BR);
        assertEquals(taxClassification.getSubdivisionCode(), subDivision);
        assertEquals(taxClassification.getType(), type);
        assertEquals(taxClassification.getNumber(), number);
    }

    @Test
    public void serializeAndDeserialize() throws IOException {
        PartyTaxRegistration partyTaxRegistration = this.getDefaultPartyTaxRegistration();
        String serialized = mapper.writeValueAsString(partyTaxRegistration);
        PartyTaxRegistration deserialized = mapper.readValue(serialized, PartyTaxRegistration.class);

        assertEquals(partyTaxRegistration, deserialized);
    }

    @Test
    public void nullValidation() {
        try {
            PartyTaxRegistration.builder().build().validate();
        } catch (InvalidModelException e) {
            assertThat(e.getPropertyErrors(), hasMandatoryItems(TYPE));
        }
    }

    @Test
    public void validationWithoutErrors() {
        PartyTaxRegistration.builder().withType("020").withNumber("5").build().validate();
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(PartyTaxRegistration.class)
            .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
            .verify();
    }

    private PartyTaxRegistration getDefaultPartyTaxRegistration() {
        return PartyTaxRegistration.builder().withCountryRegionCode(CountryCode.AD).withSubdivisionCode("subDivision")
                .withType("type").withNumber("number").build();
    }
}