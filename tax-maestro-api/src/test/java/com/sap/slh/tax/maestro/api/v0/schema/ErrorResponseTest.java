package com.sap.slh.tax.maestro.api.v0.schema;

import static org.junit.Assert.assertEquals;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.sap.slh.tax.maestro.api.v0.domain.ErrorType;
import java.io.IOException;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ErrorResponseTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void serialization() throws IOException {
        this.validateSerialization(400, ErrorType.INVALID_INPUT, "ZipCode in the request is not valid");
    }

    private void validateSerialization(int status, ErrorType errorType, String message) throws IOException {
        String expectedJson = this.buildJsonResponse(status, errorType.toString(), message);
        ErrorResponse error = ErrorResponse.builder().withStatus(status).withType(errorType).withMessage(message)
                .build();
        ErrorResponse expectedObject = new ObjectMapper().readValue(expectedJson, ErrorResponse.class);

        assertEquals(expectedObject, error);
    }

    @Test
    public void objectValidation() {
        ErrorResponse.builder().build().validate();
    }

    @Test
    public void invalidErrorTypeDomain() throws IOException {
        thrown.expect(InvalidFormatException.class);
        thrown.expectMessage("type");

        String actualJson = this.buildJsonResponse(400, "unknows", "error");
        new ObjectMapper().readValue(actualJson, ErrorResponse.class);
    }

    private String buildJsonResponse(int status, String errorType, String message) {
        return String.format("{\"status\":%d,\"type\":\"%s\",\"message\":\"%s\"}", status, errorType, message);
    }
    
    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(ErrorResponse.class)
                .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
                .verify();
    }
}
