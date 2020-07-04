package com.sap.slh.tax.maestro.api.v1.schema;

import static com.sap.slh.tax.maestro.api.assertion.HasMandatoryAttribute.hasMandatoryItems;
import static com.sap.slh.tax.maestro.api.v1.schema.PartyTaxClassification.JSONParameter.EXEMPTION_REASON_CODE;
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

public class PartyTaxClassificationTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void nullFields() {
        PartyTaxClassification partyTaxClassification = PartyTaxClassification.builder().build();

        assertNull(partyTaxClassification.getCountryRegionCode());
        assertNull(partyTaxClassification.getSubdivisionCode());
        assertNull(partyTaxClassification.getTaxTypeCode());
        assertNull(partyTaxClassification.getExemptionReasonCode());
    }

    @Test
    public void builder() {
        String taxExemptionReasonCode = "1";
        String subdivisionCode = "3";
        String taxRateTypeCode = "4";

        PartyTaxClassification taxClassification = PartyTaxClassification.builder()
                .withCountryRegionCode(CountryCode.BR).withSubdivisionCode(subdivisionCode)
                .withTaxTypeCode(taxRateTypeCode).withExemptionReasonCode(taxExemptionReasonCode).build();

        assertEquals(taxClassification.getExemptionReasonCode(), taxExemptionReasonCode);
        assertEquals(taxClassification.getCountryRegionCode(), CountryCode.BR);
        assertEquals(taxClassification.getSubdivisionCode(), subdivisionCode);
        assertEquals(taxClassification.getTaxTypeCode(), taxRateTypeCode);
    }

    @Test
    public void serializeAndDeserialize() throws IOException {
        PartyTaxClassification partyTaxClassification = this.getDefaultPartyTaxClassification();
        String serialized = mapper.writeValueAsString(partyTaxClassification);
        PartyTaxClassification deserialized = mapper.readValue(serialized, PartyTaxClassification.class);

        assertEquals(partyTaxClassification, deserialized);
    }

    @Test
    public void nullValidation() {
        try {
            PartyTaxClassification.builder().build().validate();
        } catch (InvalidModelException e) {
            assertThat(e.getPropertyErrors(), hasMandatoryItems(EXEMPTION_REASON_CODE));
        }
    }

    @Test
    public void validationWithoutErrors() {
        PartyTaxClassification.builder().withExemptionReasonCode("010").build().validate();
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(PartyTaxClassification.class)
            .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
            .verify();
    }

    private PartyTaxClassification getDefaultPartyTaxClassification() {
        return PartyTaxClassification.builder().withCountryRegionCode(CountryCode.AD)
                .withSubdivisionCode("subdivisionCode").withTaxTypeCode("taxTypeCode")
                .withExemptionReasonCode("taxExemptionReasonCode").build();
    }
}
