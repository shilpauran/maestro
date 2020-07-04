package com.sap.slh.tax.maestro.api.v1.schema;

import static com.sap.slh.tax.maestro.api.assertion.HasMandatoryAttribute.hasMandatoryItems;
import static com.sap.slh.tax.maestro.api.v1.schema.Party.JSONParameter.COUNTRY_REGION_CODE;
import static com.sap.slh.tax.maestro.api.v1.schema.Party.JSONParameter.ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class PartyTest {

    private final String DEFAULT_ID = "1";
    private final String DEFAULT_SUBDIVISION_CODE = "3";
    private final String DEFAULT_CITY = "4";
    private final String DEFAULT_COUNTY = "5";
    private final String DEFAULT_ZIP_CODE = "6";
    private final String DEFAULT_ADDRESS_LINE = "7";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void nullfields() {
        Party party = Party.builder().build();

        assertNull(party.getId());
        assertNull(party.getCountryRegionCode());
        assertNull(party.getSubdivisionCode());
        assertNull(party.getCity());
        assertNull(party.getCounty());
        assertNull(party.getTaxClassifications());
        assertNull(party.getTaxRegistrations());
        assertNull(party.getZipCode());
        assertNull(party.getAddress());
        assertNull(party.getAdditionalInformation());
    }

    @Test
    public void builder() {
        Party party = this.getDefaultParty();

        assertEquals(party.getId(), DEFAULT_ID);
        assertEquals(party.getCountryRegionCode(), CountryCode.BR);
        assertEquals(party.getSubdivisionCode(), DEFAULT_SUBDIVISION_CODE);
        assertEquals(party.getCity(), DEFAULT_CITY);
        assertEquals(party.getCounty(), DEFAULT_COUNTY);
        assertEquals(party.getZipCode(), DEFAULT_ZIP_CODE);
        assertEquals(party.getAddress(), DEFAULT_ADDRESS_LINE);
        assertEquals(party.getTaxClassifications(), Collections.emptyList());
        assertEquals(party.getTaxRegistrations(), Collections.emptyList());
        assertEquals(party.getAdditionalInformation(), Collections.emptyMap());
    }

    @Test
    public void serializeAndDeserialize() throws IOException {
        Party party = this.getDefaultParty();
        String serialized = mapper.writeValueAsString(party);
        Party deserialized = mapper.readValue(serialized, Party.class);

        assertEquals(party, deserialized);
    }

    @Test
    public void validateNull() {
        try {
            Party.builder().build().validate();
            fail();
        } catch (InvalidModelException e) {
            assertThat(e.getPropertyErrors(), hasMandatoryItems(ID, COUNTRY_REGION_CODE));
        }
    }

    @Test
    public void validateWithoutError() {
        Party.builder().withId("010").withCountryRegionCode(CountryCode.CA).build().validate();
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(Party.class).suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS).verify();
    }

    private Party getDefaultParty() {
        return Party.builder()
                .withId(DEFAULT_ID)
                .withCountryRegionCode(CountryCode.BR)
                .withSubdivisionCode(DEFAULT_SUBDIVISION_CODE)
                .withCity(DEFAULT_CITY)
                .withCounty(DEFAULT_COUNTY)
                .withZipCode(DEFAULT_ZIP_CODE)
                .withAddress(DEFAULT_ADDRESS_LINE)
                .withTaxClassifications(Collections.emptyList())
                .withTaxRegistrations(Collections.emptyList())
                .withAdditionalInformation(Collections.emptyMap())
                .build();
    }
}
