package com.sap.slh.tax.maestro.api.v0.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import java.io.IOException;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.v0.domain.BooleanValue;
import com.sap.slh.tax.maestro.api.v0.domain.LocationType;

public class LocationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private String mandatoryPropertyMessageFormat = "Mandatory property missing: '%s'";

    @Test
    public void serializationAndDeserialization() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"type\":\"CONTRACT_FROM\",\"addressLine1\":\"addressLine1\","
                + "\"addressLine2\":\"addressLine2\",\"addressLine3\":\"addressLine3\",\"zipCode\":\"zipCode\","
                + "\"city\":\"city\",\"county\":\"county\",\"country\":\"BR\",\"state\":\"state\","
                + "\"addressId\":\"addressId\",\"isCompanyTaxRegistered\":\"Y\","
                + "\"isBusinessPartnerTaxRegistered\":\"n\"}";
        Location actualObject = new ObjectMapper().readValue(actualJson, Location.class);

        Location expectedObject = Location.builder().withType(LocationType.CONTRACT_FROM)
                .withAddressLine1("addressLine1").withAddressLine2("addressLine2").withAddressLine3("addressLine3")
                .withZipCode("zipCode").withCity("city").withState("state").withCounty("county")
                .withAddressId("addressId").withCountry(CountryCode.BR).withIsCompanyTaxRegistered(BooleanValue.Y)
                .withIsBusinessPartnerTaxRegistered(BooleanValue.n).build();

        assertEquals(expectedObject, actualObject);
        assertEquals(actualObject,
                new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(expectedObject), Location.class));
    }

    @Test
    public void objectValidation() {
        try {
            Location.builder().build().validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage0 = String.format(mandatoryPropertyMessageFormat, "type");
            String expectedMessage1 = String.format(mandatoryPropertyMessageFormat, "zipCode");
            String expectedMessage2 = String.format(mandatoryPropertyMessageFormat, "country");

            assertEquals(3, ex.getPropertyErrors().size());
            assertEquals(expectedMessage0, ex.getPropertyErrors().get(0).getMessage());
            assertEquals(expectedMessage1, ex.getPropertyErrors().get(1).getMessage());
            assertEquals(expectedMessage2, ex.getPropertyErrors().get(2).getMessage());
        }
    }

    @Test
    public void validLocationTypeDomain() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"type\":\"CONTRACT_FROM\",\"addressLine1\":\"addressLine1\","
                + "\"addressLine2\":\"addressLine2\",\"addressLine3\":\"addressLine3\",\"zipCode\":\"zipCode\","
                + "\"city\":\"city\",\"county\":\"county\",\"country\":\"BR\",\"state\":\"state\","
                + "\"addressId\":\"addressId\",\"isCompanyTaxRegistered\":\"Y\","
                + "\"isBusinessPartnerTaxRegistered\":\"n\"}";
        Location actualObject = new ObjectMapper().readValue(actualJson, Location.class);

        assertEquals(actualObject.getType(), LocationType.CONTRACT_FROM);
    }

    @Test
    public void invalidLocationTypeDomain() throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("LocationType");

        String actualJson = String.format("{\"type\":\"%s\"}", "invalid");
        new ObjectMapper().readValue(actualJson, Location.class);
    }

    @Test
    public void validCountryCodeDomain() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"type\":\"CONTRACT_FROM\",\"addressLine1\":\"addressLine1\","
                + "\"addressLine2\":\"addressLine2\",\"addressLine3\":\"addressLine3\",\"zipCode\":\"zipCode\","
                + "\"city\":\"city\",\"county\":\"county\",\"country\":\"BR\",\"state\":\"state\","
                + "\"addressId\":\"addressId\",\"isCompanyTaxRegistered\":\"Y\","
                + "\"isBusinessPartnerTaxRegistered\":\"n\"}";
        Location actualObject = new ObjectMapper().readValue(actualJson, Location.class);

        assertEquals(actualObject.getCountry(), CountryCode.BR);
    }

    @Test
    public void invalidCountryCountryDomain() throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("CountryCode");

        String actualJson = String.format("{\"country\":\"%s\"}", "invalid");
        new ObjectMapper().readValue(actualJson, Location.class);
    }

    @Test
    public void validIsCompanyTaxRegisteredDomain() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"type\":\"CONTRACT_FROM\",\"addressLine1\":\"addressLine1\","
                + "\"addressLine2\":\"addressLine2\",\"addressLine3\":\"addressLine3\",\"zipCode\":\"zipCode\","
                + "\"city\":\"city\",\"county\":\"county\",\"country\":\"BR\",\"state\":\"state\","
                + "\"addressId\":\"addressId\",\"isCompanyTaxRegistered\":\"n\","
                + "\"isBusinessPartnerTaxRegistered\":\"n\"}";
        Location actualObject = new ObjectMapper().readValue(actualJson, Location.class);

        assertEquals(actualObject.getIsCompanyTaxRegistered(), BooleanValue.n);
    }

    @Test
    public void invalidIsCompanyTaxRegisteredDomain() throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("BooleanValue");

        String actualJson = String.format("{\"isCompanyTaxRegistered\":\"%s\"}", "invalid");
        new ObjectMapper().readValue(actualJson, Location.class);
    }

    @Test
    public void validIsBusinessPartnerTaxRegisteredDomain()
            throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"type\":\"CONTRACT_FROM\",\"addressLine1\":\"addressLine1\","
                + "\"addressLine2\":\"addressLine2\",\"addressLine3\":\"addressLine3\",\"zipCode\":\"zipCode\","
                + "\"city\":\"city\",\"county\":\"county\",\"country\":\"BR\",\"state\":\"state\","
                + "\"addressId\":\"addressId\",\"isCompanyTaxRegistered\":\"Y\","
                + "\"isBusinessPartnerTaxRegistered\":\"n\"}";
        Location actualObject = new ObjectMapper().readValue(actualJson, Location.class);

        assertEquals(actualObject.getIsBusinessPartnerTaxRegistered(), BooleanValue.n);
    }

    @Test
    public void invalidIsBusinessPartnerTaxRegisteredDomain()
            throws JsonParseException, JsonMappingException, IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("BooleanValue");

        String actualJson = String.format("{\"isBusinessPartnerTaxRegistered\":\"%s\"}", "invalid");
        new ObjectMapper().readValue(actualJson, Location.class);
    }

    @Test
    public void validateMissingZipCode() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"type\":\"CONTRACT_FROM\",\"addressLine1\":\"addressLine1\","
                + "\"addressLine2\":\"addressLine2\",\"addressLine3\":\"addressLine3\",\"city\":\"city\","
                + "\"county\":\"county\",\"country\":\"BR\",\"state\":\"state\",\"addressId\":\"addressId\","
                + "\"isCompanyTaxRegistered\":\"Y\",\"isBusinessPartnerTaxRegistered\":\"n\"}";

        try {
            new ObjectMapper().readValue(actualJson, Location.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "zipCode");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateNullZipCode() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"type\":\"CONTRACT_FROM\",\"addressLine1\":\"addressLine1\","
                + "\"addressLine2\":\"addressLine2\",\"addressLine3\":\"addressLine3\",\"zipCode\":null,"
                + "\"city\":\"city\",\"county\":\"county\",\"country\":\"BR\",\"state\":\"state\","
                + "\"addressId\":\"addressId\",\"isCompanyTaxRegistered\":\"Y\","
                + "\"isBusinessPartnerTaxRegistered\":\"n\"}";

        try {
            new ObjectMapper().readValue(actualJson, Location.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "zipCode");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateEmptyZipCode() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"type\":\"CONTRACT_FROM\",\"addressLine1\":\"addressLine1\","
                + "\"addressLine2\":\"addressLine2\",\"addressLine3\":\"addressLine3\",\"zipCode\":\"\","
                + "\"city\":\"city\",\"county\":\"county\",\"country\":\"BR\",\"state\":\"state\","
                + "\"addressId\":\"addressId\",\"isCompanyTaxRegistered\":\"Y\","
                + "\"isBusinessPartnerTaxRegistered\":\"n\"}";

        try {
            new ObjectMapper().readValue(actualJson, Location.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "zipCode");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateMissingCountry() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"type\":\"CONTRACT_FROM\",\"addressLine1\":\"addressLine1\","
                + "\"addressLine2\":\"addressLine2\",\"addressLine3\":\"addressLine3\",\"zipCode\":\"zipCode\","
                + "\"city\":\"city\",\"county\":\"county\",\"state\":\"state\",\"addressId\":\"addressId\","
                + "\"isCompanyTaxRegistered\":\"Y\",\"isBusinessPartnerTaxRegistered\":\"n\"}";

        try {
            new ObjectMapper().readValue(actualJson, Location.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "country");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateNullCountry() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"type\":\"CONTRACT_FROM\",\"addressLine1\":\"addressLine1\","
                + "\"addressLine2\":\"addressLine2\",\"addressLine3\":\"addressLine3\",\"zipCode\":\"zipCode\","
                + "\"city\":\"city\",\"county\":\"county\",\"country\":null,\"state\":\"state\","
                + "\"addressId\":\"addressId\",\"isCompanyTaxRegistered\":\"Y\","
                + "\"isBusinessPartnerTaxRegistered\":\"n\"}";

        try {
            new ObjectMapper().readValue(actualJson, Location.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "country");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateMissingType() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"addressLine1\":\"addressLine1\",\"addressLine2\":\"addressLine2\","
                + "\"addressLine3\":\"addressLine3\",\"zipCode\":\"zipCode\",\"city\":\"city\",\"county\":\"county\","
                + "\"country\":\"BR\",\"state\":\"state\",\"addressId\":\"addressId\",\"isCompanyTaxRegistered\":\"Y\","
                + "\"isBusinessPartnerTaxRegistered\":\"n\"}";

        try {
            new ObjectMapper().readValue(actualJson, Location.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "type");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void validateNullType() throws JsonParseException, JsonMappingException, IOException {
        String actualJson = "{\"type\":null,\"addressLine1\":\"addressLine1\",\"addressLine2\":\"addressLine2\","
                + "\"addressLine3\":\"addressLine3\",\"zipCode\":\"zipCode\",\"city\":\"city\",\"county\":\"county\","
                + "\"country\":\"BR\",\"state\":\"state\",\"addressId\":\"addressId\",\"isCompanyTaxRegistered\":\"Y\","
                + "\"isBusinessPartnerTaxRegistered\":\"n\"}";

        try {
            new ObjectMapper().readValue(actualJson, Location.class).validate();
            fail("Expected Excetion InvalidModelException was not thrown");
        } catch (InvalidModelException ex) {
            String expectedMessage = String.format(mandatoryPropertyMessageFormat, "type");
            assertEquals(1, ex.getPropertyErrors().size());
            assertEquals(expectedMessage, ex.getPropertyErrors().get(0).getMessage());
        }
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(Location.class)
            .suppress(nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE, nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS)
            .verify();
    }
}
