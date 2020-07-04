package com.sap.slh.tax.maestro.api.v1.schema;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.MessageFormat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class ErrorDetailTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ObjectMapper mapper = new ObjectMapper();

    String defaultMessage = "'USF' is an invalid value for property 'currency'";

    @Test
    public void serialization() throws IOException {
        ErrorDetail errorDetail = this.buildErrorDetail(defaultMessage);
        ErrorDetail serializedErrorDetail = this.mapErrorDetail(defaultMessage);

        assertEquals(errorDetail, serializedErrorDetail);
    }

    @Test
    public void objectValidation() {
        ErrorDetail.builder().withMessage("Message").build().validate();
    }

    @Test
    public void objectValidationError() {
        thrown.expect(InvalidModelException.class);
        ErrorDetail.builder().build().validate();
    }

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(ErrorDetail.class)
            .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
            .verify();
    }

    private ErrorDetail buildErrorDetail(String message) {
        return ErrorDetail.builder().withMessage(message).build();
    }

    private ErrorDetail mapErrorDetail(String message) throws IOException {
        String jsonErrorDetail = this.getJsonString(message);
        return mapper.readValue(jsonErrorDetail, ErrorDetail.class);
    }

    private String getJsonString(String message) {
        return MessageFormat.format("'{' \"message\": \"{0}\"'}'", message);
    }
}
