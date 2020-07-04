package com.sap.slh.tax.maestro.tax.controller.handler;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebInputException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.sap.slh.tax.attributes.determination.model.api.ValidationErrorCode;
import com.sap.slh.tax.calculation.model.common.TaxCalculationErrorMessage;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorMandatoryMissingDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorMandatoryValueMissingDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorReferenceDetail;
import com.sap.slh.tax.maestro.api.v0.domain.ErrorType;
import com.sap.slh.tax.maestro.api.v0.domain.GrossOrNet;
import com.sap.slh.tax.maestro.api.v0.domain.ItemType;
import com.sap.slh.tax.maestro.api.v0.schema.ErrorResponse;
import com.sap.slh.tax.maestro.context.tenant.TenantNotRetrievedException;
import com.sap.slh.tax.maestro.tax.controller.MessageSourceMock;
import com.sap.slh.tax.maestro.tax.exceptions.DestinationBadRequestException;
import com.sap.slh.tax.maestro.tax.exceptions.DirectPayloadNotSupportedException;
import com.sap.slh.tax.maestro.tax.exceptions.JMXException;
import com.sap.slh.tax.maestro.tax.exceptions.NoAmqpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.NoJwtProvidedException;
import com.sap.slh.tax.maestro.tax.exceptions.NoRelevantCountryForDestinationException;
import com.sap.slh.tax.maestro.tax.exceptions.QueueCommunicationException;
import com.sap.slh.tax.maestro.tax.exceptions.UnknownAmqpResponseTypeException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.CalculateInvalidModelException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.CalculateNoContentException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.CalculatePartialContentException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.ErrorDetail;
import com.sap.slh.tax.maestro.tax.exceptions.determine.DetermineInvalidModelException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.DetermineNoContentException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.DeterminePartialContentException;

public class ResponseErrorV0HandlerTest {
    private static ResponseErrorV0Handler handler;

    @BeforeClass
    public static void classSetup() {
        MessageSourceMock msgSource = new MessageSourceMock();
        handler = new ResponseErrorV0Handler(msgSource);
    }

    @Test
    public void tenantNotRetrievedException() {
        TenantNotRetrievedException exception = new TenantNotRetrievedException();

        ErrorResponse error = handler.handleTenantNotRetrievedException(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.SERVER_EXCEPTION)
                .withMessage(
                        "Tenant identification could not be resolved. If this error persists, open a support request.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void noAmqpResponseException() {
        NoAmqpResponseException exception = new NoAmqpResponseException("Exchange1", "RoutingKey1");

        ErrorResponse error = handler.handleNoAmqpResponseException(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.SERVER_EXCEPTION)
                .withMessage("An unexpected error occurred while processing your request. Open a support request.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void JMXException() {
        JMXException exception = new JMXException("JMX Error");

        ErrorResponse error = handler.handleJMXException(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.SERVER_EXCEPTION)
                .withMessage("An unexpected error occurred while processing your request. Open a support request.")
                .build();

        assertEquals(expected, error);
    }    
    
    @Test
    public void taxException() {
        UnknownAmqpResponseTypeException exception = new UnknownAmqpResponseTypeException();

        ErrorResponse error = handler.handleUnexpectedTaxException(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.SERVER_EXCEPTION)
                .withMessage("An unexpected error occurred while processing your request. Open a support request.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void mandatoryAttributesMissingError() {
        List<PropertyErrorDetail> propertyErrors = Arrays.asList(new PropertyErrorMandatoryMissingDetail("Property1"),
                new PropertyErrorMandatoryMissingDetail("Property2"));
        InvalidModelException exception = new InvalidModelException(propertyErrors);

        ErrorResponse error = handler.handleMandatoryAttributesMissingError(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage(
                        "The 'Property1' parameter is mandatory in a request. The 'Property2' parameter is mandatory in a request.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void mandatoryAttributeValueMissingError() {
        List<PropertyErrorDetail> propertyErrors = Arrays
                .asList(new PropertyErrorMandatoryValueMissingDetail("Property1", "value1, value2"));
        InvalidModelException exception = new InvalidModelException(propertyErrors);

        ErrorResponse error = handler.handleMandatoryAttributesMissingError(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage(
                        "The 'Property1' parameter, with one of the following values: value1, value2, is mandatory in this request. Add this parameter in an entry.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void mandatoryMultiLevelAttributesMissingError() {
        PropertyErrorMandatoryMissingDetail error1 = new PropertyErrorMandatoryMissingDetail("Property");
        error1.addReferencePropertyErrorDetail(new PropertyErrorReferenceDetail("List"));

        PropertyErrorMandatoryMissingDetail error2 = new PropertyErrorMandatoryMissingDetail("Property");
        error2.addReferencePropertyErrorDetail(new PropertyErrorReferenceDetail("List", "1"));
        PropertyErrorMandatoryMissingDetail error3 = new PropertyErrorMandatoryMissingDetail("Property");
        error3.addReferencePropertyErrorDetail(new PropertyErrorReferenceDetail("List", "1"));
        error3.addReferencePropertyErrorDetail(new PropertyErrorReferenceDetail("List", "2"));

        List<PropertyErrorDetail> propertyErrors = Arrays.asList(error1, error2, error3);
        InvalidModelException exception = new InvalidModelException(propertyErrors);

        ErrorResponse error = handler.handleMandatoryAttributesMissingError(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage(
                        "The 'Property' parameter is mandatory in a request, under 'List'. The 'Property' parameter is mandatory in a request, under 'List' 1. The 'Property' parameter is mandatory in a request, under 'List' 1, under 'List' 2.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void decodingError() {
        ErrorResponse error = handler.handleDecodingError(new DecodingException("an error"), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.BAD_REQUEST)
                .withMessage("an error")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void anyOtherException() {
        ErrorResponse error = handler.handleUnexpectedException(new Exception(), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.SERVER_EXCEPTION)
                .withMessage("An unexpected error occurred while processing your request. Open a support request.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void queueCommunicationException() {
        ErrorResponse error = handler
                .handleQueueCommunicationException(new QueueCommunicationException("Error message."), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.SERVER_EXCEPTION)
                .withMessage("An unexpected error occurred while processing your request. Open a support request.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void unknownAmqpResponseTypeException() {
        ErrorResponse error = handler.handleUnknowAmqpResponseType(new UnknownAmqpResponseTypeException(), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.SERVER_EXCEPTION)
                .withMessage("An unexpected error occurred while processing your request. Open a support request.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void handleJsonParseException() {
        JsonParseException exception = new JsonParseException(null, "");

        ErrorResponse error = handler.handleInvalidJson(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage("The request JSON is invalid, review your request.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void handleJsonMappingException() {
        JsonMappingException exception = new JsonMappingException(null, "");

        ErrorResponse error = handler.handleInvalidJson(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage("The request JSON is invalid, review your request.")
                .build();

        assertEquals(expected, error);
    }    
    
    @Test
    public void emptyPayloadError() {
        ServerWebInputException exception = new ServerWebInputException("");

        ErrorResponse error = handler.handleInvalidJson(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage("The request JSON is invalid, review your request.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void invalidEnumTypeOnJSON() throws IOException {
        JsonParser jsonParser = Mockito.mock(JsonParser.class);
        Mockito.doReturn(JsonToken.VALUE_STRING).when(jsonParser).getCurrentToken();
        Mockito.doReturn("WRONG").when(jsonParser).getText();

        MismatchedInputException exception = MismatchedInputException.from(jsonParser, GrossOrNet.class, null);
        exception.prependPath(new Reference(null, "grossOrNet"));

        ErrorResponse error = handler.handleInvalidFormatError(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage(
                        "'WRONG' is an invalid value for JSON parameter 'grossOrNet'. Use one of the following values: g, n, G, N.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void invalidNestedEnumTypeOnJSON() throws IOException {
        JsonParser jsonParser = Mockito.mock(JsonParser.class);
        Mockito.doReturn(JsonToken.VALUE_STRING).when(jsonParser).getCurrentToken();
        Mockito.doReturn("WRONG").when(jsonParser).getText();

        MismatchedInputException exception = MismatchedInputException.from(jsonParser, ItemType.class, null);
        exception.prependPath(new Reference(null, "ItemType"));
        exception.prependPath(new Reference(null, 0));
        exception.prependPath(new Reference(null, "Items"));

        ErrorResponse error = handler.handleInvalidFormatError(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage(
                        "'WRONG' is an invalid value for JSON parameter 'Items[0].ItemType'. Use one of the following values: m, M, s, S.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void noRelevantCountryForDestination() {

        ErrorResponse error = handler.handleNoRelevantCountryForDestination(
                new NoRelevantCountryForDestinationException("Error"), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage("We could not find a relevant country or region to get the destination for this request.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void directPayloadNotSupportedException() {

        ErrorResponse error = handler
                .handleDirectPayloadNotSupportedException(new DirectPayloadNotSupportedException("Error"), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage(
                        "Tax Service calculation engine currently does not support direct payload requests. To calculate the taxes using the direct payload request, you can contract an SAP tax partner that supports this feature.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void jwtNotProvidedException() {
        NoJwtProvidedException exception = new NoJwtProvidedException();

        ErrorResponse error = handler.handleJwtNotProvided(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.BAD_REQUEST)
                .withMessage("Provide JSON Web Tokens (JWT) in the payload request header.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void determineNoContentException() {
        DetermineNoContentException exception = new DetermineNoContentException("Content not found", Collections.emptyList());

        ErrorResponse error = handler.handleDetermineNoContentError(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.NO_CONTENT.value())
                .withType(ErrorType.NO_CONTENT)
                .withMessage("Content not found")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void determinePartialContentException() {
        List<com.sap.slh.tax.maestro.tax.exceptions.determine.ErrorDetail> errorDetails = Arrays
                .asList(new com.sap.slh.tax.maestro.tax.exceptions.determine.ErrorDetail(
                        ValidationErrorCode.INSUFFICIENT_PARTIES, "Insufficient parties."));
        
        DeterminePartialContentException exception = new DeterminePartialContentException("Partial content found.", errorDetails);

        ErrorResponse error = handler.handleDeterminePartialContentError(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.PARTIAL_CONTENT.value())
                .withType(ErrorType.PARTIAL_CONTENT)
                .withMessage("Partial content found. Insufficient parties.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void determineInvalidModelExceptionShouldThrowBadRequest() {
        List<com.sap.slh.tax.maestro.tax.exceptions.determine.ErrorDetail> errorDetails = Arrays.asList(
                new com.sap.slh.tax.maestro.tax.exceptions.determine.ErrorDetail(
                        ValidationErrorCode.DATE_DOES_NOT_EXIST, "Date does not exist."),
                new com.sap.slh.tax.maestro.tax.exceptions.determine.ErrorDetail(
                        ValidationErrorCode.ERROR_PRODUCT_ID_REQUIRED, "Product id required."));

        DetermineInvalidModelException exception = new DetermineInvalidModelException(
                "We were not able to fetch the tax attributes for this request; please check the errors for details.",
                errorDetails);

        ResponseEntity<ErrorResponse> error = handler.handleDetermineInvalidModelError(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage("We were not able to fetch the tax attributes for this request; please check the errors for details. Date does not exist.")
                .build();

        ResponseEntity<ErrorResponse> expectedError = new ResponseEntity<>(expected, HttpStatus.BAD_REQUEST);

        assertEquals(expectedError, error);
    }

    @Test
    public void determineInvalidModelExceptionShouldThrowInternalServerError() {
        List<com.sap.slh.tax.maestro.tax.exceptions.determine.ErrorDetail> errorDetails = Arrays.asList(
                new com.sap.slh.tax.maestro.tax.exceptions.determine.ErrorDetail(
                        ValidationErrorCode.ERROR_PRODUCT_ID_REQUIRED, "Product id required."),
                new com.sap.slh.tax.maestro.tax.exceptions.determine.ErrorDetail(ValidationErrorCode.INVALID_PRODUCT_ID,
                        "Invalid product id."));

        DetermineInvalidModelException exception = new DetermineInvalidModelException(
                "We were not able to fetch the tax attributes for this request; please check the errors for details.",
                errorDetails);

        ResponseEntity<ErrorResponse> error = handler.handleDetermineInvalidModelError(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.SERVER_EXCEPTION)
                .withMessage("An unexpected error occurred while processing your request. Open a support request.")
                .build();

        ResponseEntity<ErrorResponse> expectedError = new ResponseEntity<>(expected, HttpStatus.INTERNAL_SERVER_ERROR);

        assertEquals(expectedError, error);
    }

    @Test
    public void destinationBadRequestExceptionShouldThrowBadRequest() {
        DestinationBadRequestException exception = new DestinationBadRequestException(
                "There is more than one Destination"
                        + " that matches the 'GB' countryRegion code. Verify the configurations for Destinations in SAP Cloud Platform.");

        ErrorResponse error = handler.handleDestinationBadRequestError(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_CONTENT)
                .withMessage("There is more than one Destination that matches the 'GB' countryRegion code. Verify the"
                        + " configurations for Destinations in SAP Cloud Platform.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void calculateNoContentException() {
        List<ErrorDetail> errorDetails = Arrays
                .asList(new ErrorDetail(TaxCalculationErrorMessage.ITEM_OBJECT_INVALID, "Item object invalid."));

        CalculateNoContentException exception = new CalculateNoContentException("Content not found.", errorDetails);

        ErrorResponse error = handler.handleCalculateNoContentError(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.NO_CONTENT.value())
                .withType(ErrorType.NO_CONTENT)
                .withMessage("Content not found. Item object invalid.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void calculatePartialContentException() {
        CalculatePartialContentException exception = new CalculatePartialContentException("Partial content found.",
                Collections.emptyList());

        ErrorResponse error = handler.handleCalculatePartialContentError(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.PARTIAL_CONTENT.value())
                .withType(ErrorType.PARTIAL_CONTENT)
                .withMessage("Partial content found.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void calculateInvalidModelExceptionShouldThrowBadRequest() {
        List<ErrorDetail> errorDetails = Arrays.asList(
                new ErrorDetail(TaxCalculationErrorMessage.DATE_INVALID, "Date invalid."),
                new ErrorDetail(TaxCalculationErrorMessage.ERROR_DOCUMENT_ID_REQUIRED, "Document ID cannot be blank."));

        CalculateInvalidModelException exception = new CalculateInvalidModelException("Bad request.", errorDetails);

        ResponseEntity<ErrorResponse> error = handler.handleCalculateInvalidModelError(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage("Bad request. Date invalid.")
                .build();

        ResponseEntity<ErrorResponse> expectedError = new ResponseEntity<>(expected, HttpStatus.BAD_REQUEST);

        assertEquals(expectedError, error);
    }

    @Test
    public void calculateInvalidModelExceptionShouldThrowInternalServerError() {
        List<ErrorDetail> errorDetails = Arrays.asList(
                new ErrorDetail(TaxCalculationErrorMessage.ITEM_ID_EXISTS, "Item Id already exists in the document."),
                new ErrorDetail(TaxCalculationErrorMessage.TAX_ID_EXISTS,
                        "Tax Id already exists for the Item in the document."));

        CalculateInvalidModelException exception = new CalculateInvalidModelException("Bad request.", errorDetails);

        ResponseEntity<ErrorResponse> error = handler.handleCalculateInvalidModelError(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.SERVER_EXCEPTION)
                .withMessage("An unexpected error occurred while processing your request. Open a support request.")
                .build();

        ResponseEntity<ErrorResponse> expectedError = new ResponseEntity<>(expected, HttpStatus.INTERNAL_SERVER_ERROR);

        assertEquals(expectedError, error);
    }
    
    @Test
    public void dataBufferLimitException() {
        DataBufferLimitException exception = new DataBufferLimitException("Payload limit exceeded");

        ErrorResponse error = handler.handleDataBufferLimitException(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage("The JSON file of your payload request is too large. Reduce its size and send  the request again. For more information see Protection Against Denial-of-Service (DoS) Attacks.")
                .build();

        assertEquals(expected, error);
    }
}
