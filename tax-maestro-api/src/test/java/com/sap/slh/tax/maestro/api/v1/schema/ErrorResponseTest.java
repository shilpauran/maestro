package com.sap.slh.tax.maestro.api.v1.schema;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class ErrorResponseTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private ObjectMapper mapper = new ObjectMapper();

	Integer defaultStatus = 400;
	String defaultMessage = "Multiple errors";
	List<ErrorDetail> defaultErrorDetails = new ArrayList<ErrorDetail>(Arrays.asList(
			ErrorDetail.builder().withMessage("'USF' is an invalid value for field 'currency'").build(),
			ErrorDetail.builder().withMessage("'f' is an invalid value for field 'Items.[0].itemType'").build()));
	String defaultDocumentation = "www.help.sap.com/taxservice/api_reference";

	@Test
	public void serialization() throws IOException {
		ErrorResponse errorResponse = this.buildErrorResponse(defaultStatus, defaultMessage, defaultErrorDetails);
		ErrorResponse serializedErrorResponse = this.mapErrorResponse(defaultStatus, defaultMessage,
				defaultErrorDetails);

		assertEquals(errorResponse, serializedErrorResponse);
		assertEquals(errorResponse.hashCode(), serializedErrorResponse.hashCode());
		assertEquals(errorResponse.toString(), serializedErrorResponse.toString());
	}

	@Test
	public void serializationErrorDetailSingle() throws IOException {
		ErrorResponse errorResponse = this.buildErrorResponse(defaultStatus, defaultMessage, defaultErrorDetails);
		ErrorResponse serializedErrorResponse = ErrorResponse.builder().withStatus(defaultStatus)
				.withMessage(defaultMessage).withErrorDetails(defaultErrorDetails.get(0), defaultErrorDetails.get(1)).build();

		assertEquals(errorResponse, serializedErrorResponse);
	}

	@Test
	public void objectValidation() {
		ErrorResponse.builder().withStatus(500).withMessage("Error").build().validate();
	}

	@Test
	public void objectValidationError() {
		thrown.expect(InvalidModelException.class);
		ErrorResponse.builder().build().validate();
	}

    @Test
    public void equalsCheck() {
        EqualsVerifier.forClass(ErrorResponse.class)
                .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
                .verify();
    }

	private ErrorResponse buildErrorResponse(Integer status, String message, List<ErrorDetail> errorDetails) {
		return ErrorResponse.builder().withStatus(status).withMessage(message).withErrorDetails(errorDetails).build();
	}

	private ErrorResponse mapErrorResponse(Integer status, String message, List<ErrorDetail> errorDetails)
			throws IOException {
		String jsonErrorResponse = this.getJsonString(status, message, errorDetails);
		return mapper.readValue(jsonErrorResponse, ErrorResponse.class);
	}

	private String getJsonString(Integer status, String message, List<ErrorDetail> errorDetails) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(status != null ? MessageFormat.format("\"status\":{0}", status) : "");
		sb.append(message != null ? MessageFormat.format(", \"message\":\"{0}\"", message) : "");
		sb.append(errorDetails != null
				? MessageFormat.format(", \"errorDetails\":{0}", mapper.writeValueAsString(errorDetails))
				: "");
		sb.append("}");

		return sb.toString();
	}
}
