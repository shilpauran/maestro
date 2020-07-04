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
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorMutualExclusionDetail;
import com.sap.slh.tax.maestro.api.common.exception.PropertyErrorReferenceDetail;
import com.sap.slh.tax.maestro.api.v1.domain.ProductType;
import com.sap.slh.tax.maestro.api.v1.domain.TransactionType;
import com.sap.slh.tax.maestro.api.v1.schema.ErrorDetail;
import com.sap.slh.tax.maestro.api.v1.schema.ErrorResponse;
import com.sap.slh.tax.maestro.context.tenant.TenantNotRetrievedException;
import com.sap.slh.tax.maestro.tax.controller.MessageSourceMock;
import com.sap.slh.tax.maestro.tax.exceptions.JMXException;
import com.sap.slh.tax.maestro.tax.exceptions.NoAmqpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.NoJwtProvidedException;
import com.sap.slh.tax.maestro.tax.exceptions.QueueCommunicationException;
import com.sap.slh.tax.maestro.tax.exceptions.UnknownAmqpResponseTypeException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.CalculateInvalidModelException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.CalculateNoContentException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.CalculatePartialContentException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.DetermineInvalidModelException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.DetermineNoContentException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.DeterminePartialContentException;
import com.sap.slh.tax.maestro.tax.exceptions.enhance.EnhanceBadRequestException;

public class ResponseErrorV1HandlerTest {

    private static ResponseErrorV1Handler handler;

    @BeforeClass
    public static void classSetup() {
        MessageSourceMock msgSource = new MessageSourceMock();
        handler = new ResponseErrorV1Handler(msgSource);
    }

    @Test
    public void tenantNotRetrievedException() {
        ErrorResponse error = handler.handleTenantNotRetrievedException(new TenantNotRetrievedException(), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withMessage(
                        "Tenant identification could not be resolved. If this error persists, open a support request.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void noAmqpResponseException() {
        ErrorResponse error = handler
                .handleNoAmqpResponseException(new NoAmqpResponseException("Exchange1", "RoutingKey1"), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withMessage("An unexpected error occurred while processing your request. Open a support request.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void JMXException() {
        ErrorResponse error = handler
                .handleJMXException(new JMXException("JMX Error"), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
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
                .withMessage("An unexpected error occurred while processing your request. Open a support request.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void unknownAmqpResponseTypeException() {
        ErrorResponse error = handler.handleUnknowAmqpResponseType(new UnknownAmqpResponseTypeException(), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withMessage("An unexpected error occurred while processing your request. Open a support request.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void taxException() {
        ErrorResponse error = handler.handleUnexpectedTaxException(new UnknownAmqpResponseTypeException(), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
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
                .withMessage("The format of your request is invalid, review your request.")
                .withErrorDetails(
                        ErrorDetail.builder()
                                .withMessage("The 'Property1' parameter is mandatory in a request.")
                                .build(),
                        ErrorDetail.builder()
                                .withMessage("The 'Property2' parameter is mandatory in a request.")
                                .build())
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
        PropertyErrorReferenceDetail ref1 = new PropertyErrorReferenceDetail("List", "1");
        PropertyErrorReferenceDetail ref2 = new PropertyErrorReferenceDetail("List", "2");
        error3.addReferencePropertyErrorDetail(ref1);
        error3.addReferencePropertyErrorDetail(ref2);

        List<PropertyErrorDetail> propertyErrors = Arrays.asList(error1, error2, error3);
        InvalidModelException exception = new InvalidModelException(propertyErrors);

        ErrorResponse error = handler.handleMandatoryAttributesMissingError(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withMessage("The format of your request is invalid, review your request.")
                .withErrorDetails(
                        ErrorDetail.builder()
                                .withMessage("The 'Property' parameter is mandatory in a request, under 'List'.")
                                .build(),
                        ErrorDetail.builder()
                                .withMessage("The 'Property' parameter is mandatory in a request, under 'List' 1.")
                                .build(),
                        ErrorDetail.builder()
                                .withMessage(
                                        "The 'Property' parameter is mandatory in a request, under 'List' 1, under 'List' 2.")
                                .build())
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void decodingError() {
        ErrorResponse error = handler.handleDecodingError(new DecodingException("This is an error"), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withMessage("This is an error")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void anyOtherException() {
        Exception exception = new Exception();

        ErrorResponse error = handler.handleUnexpectedException(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withMessage("An unexpected error occurred while processing your request. Open a support request.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void mutualExclusionError() {
        List<PropertyErrorDetail> propertyErrors = Arrays
                .asList(new PropertyErrorMutualExclusionDetail("Property1", Arrays.asList("value1", "value2")));
        InvalidModelException exception = new InvalidModelException(propertyErrors);

        ErrorResponse error = handler.handleMandatoryAttributesMissingError(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withMessage("The format of your request is invalid, review your request.")
                .withErrorDetails(ErrorDetail.builder()
                        .withMessage(
                                "Provide only one of the following attributes for the 'Property1' parameter: [value1; value2].")
                        .build())
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void handleJsonParseException() {
        JsonParseException exception = new JsonParseException(null, "");

        ErrorResponse error = handler.handleInvalidJson(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
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
                .withMessage("The request JSON is invalid, review your request.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void invalidEnumTypeOnJSON() throws IOException {
        JsonParser jsonParser = Mockito.mock(JsonParser.class);
        Mockito.doReturn(JsonToken.VALUE_STRING).when(jsonParser).getCurrentToken();
        Mockito.doReturn("P").when(jsonParser).getText();

        MismatchedInputException exception = MismatchedInputException.from(jsonParser, TransactionType.class, null);
        exception.prependPath(new Reference(null, "transactionTypeCode"));

        ErrorResponse error = handler.handleInvalidFormatError(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withMessage(
                        "'P' is an invalid value for JSON parameter 'transactionTypeCode'. Use one of the following values: SALE, PURCHASE.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void invalidValueTypeOnJSON() throws IOException {
        JsonParser jsonParser = Mockito.mock(JsonParser.class);
        Mockito.doReturn(JsonToken.VALUE_STRING).when(jsonParser).getCurrentToken();
        Mockito.doReturn("1").when(jsonParser).getText();

        MismatchedInputException exception = MismatchedInputException.from(jsonParser, Boolean.class, null);
        exception.prependPath(new Reference(null, "isCompanyDeferredTaxEnabled"));

        ErrorResponse error = handler.handleInvalidFormatError(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withMessage(
                        "'1' is an invalid type for JSON parameter 'isCompanyDeferredTaxEnabled'. Use type boolean.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void invalidNestedEnumTypeOnJSON() throws IOException {
        JsonParser jsonParser = Mockito.mock(JsonParser.class);
        Mockito.doReturn(JsonToken.VALUE_STRING).when(jsonParser).getCurrentToken();
        Mockito.doReturn("S").when(jsonParser).getText();

        MismatchedInputException exception = MismatchedInputException.from(jsonParser, ProductType.class, null);
        exception.prependPath(new Reference(null, "typeCode"));
        exception.prependPath(new Reference(null, 0));
        exception.prependPath(new Reference(null, "products"));

        ErrorResponse error = handler.handleInvalidFormatError(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withMessage(
                        "'S' is an invalid value for JSON parameter 'products[0].typeCode'. Use one of the following values: SERVICE, MATERIAL.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void jwtNotProvidedException() {
        ErrorResponse error = handler.handleJwtNotProvided(new NoJwtProvidedException(), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withMessage("Provide JSON Web Tokens (JWT) in the payload request header.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void determineNoContentException() {
        List<com.sap.slh.tax.maestro.tax.exceptions.determine.ErrorDetail> errorDetails = Arrays
                .asList(new com.sap.slh.tax.maestro.tax.exceptions.determine.ErrorDetail(
                        ValidationErrorCode.INSUFFICIENT_PARTIES, "Insufficient parties."));
        
        ErrorResponse error = handler
                .handleDetermineNoContentError(new DetermineNoContentException("Content not found.", errorDetails), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.NO_CONTENT.value())
                .withMessage("Content not found.")
                .withErrorDetails(ErrorDetail.builder().withMessage("Insufficient parties.").build())
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void determinePartialContentException() {
        ErrorResponse error = handler.handleDeterminePartialContentError(
                new DeterminePartialContentException("Partial content found.", Collections.emptyList()), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.PARTIAL_CONTENT.value())
                .withMessage("Partial content found.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void determinationInvalidModelException() {
        List<com.sap.slh.tax.maestro.tax.exceptions.determine.ErrorDetail> errorDetails = Arrays.asList(
                new com.sap.slh.tax.maestro.tax.exceptions.determine.ErrorDetail(
                        ValidationErrorCode.ERROR_DOCUMENT_ID_REQUIRED, "Document id required."),
                new com.sap.slh.tax.maestro.tax.exceptions.determine.ErrorDetail(
                        ValidationErrorCode.INSUFFICIENT_PARTIES, "Insufficient parties."));

        ResponseEntity<ErrorResponse> error = handler
                .handleDetermineInvalidModelError(new DetermineInvalidModelException(
                        "We were not able to fetch the tax attributes for this request; please check the errors for details.",
                        errorDetails), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withMessage("The format of your request is invalid, review your request.")
                .withErrorDetails(ErrorDetail.builder().withMessage("Document id required.").build(),
                        ErrorDetail.builder().withMessage("Insufficient parties.").build())
                .build();

        ResponseEntity<ErrorResponse> expectedError = new ResponseEntity<>(expected, HttpStatus.BAD_REQUEST);

        assertEquals(expectedError, error);
    }

    @Test
    public void enhanceBadRequestException() {
        List<String> errorDetails = Arrays.asList("No masterdata found for product.", "details2.");

        ErrorResponse error = handler.handleEnhanceBadRequestError(
                new EnhanceBadRequestException("No product Id found.", errorDetails), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withMessage("No product Id found.")
                .withErrorDetails(ErrorDetail.builder().withMessage("No masterdata found for product.").build(),
                        ErrorDetail.builder().withMessage("details2.").build())
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void calculateNoContentException() {
        ErrorResponse error = handler.handleCalculateNoContentError(
                new CalculateNoContentException("Content not found", Collections.emptyList()), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.NO_CONTENT.value())
                .withMessage("Content not found")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void calculatePartialContentException() {
        List<com.sap.slh.tax.maestro.tax.exceptions.calculate.ErrorDetail> errorDetails = Arrays
                .asList(new com.sap.slh.tax.maestro.tax.exceptions.calculate.ErrorDetail(
                        TaxCalculationErrorMessage.LINE_ITEM_INVALID, "Line Item invalid."));

        ErrorResponse error = handler.handleCalculatePartialContentError(
                new CalculatePartialContentException("Partial content found.", errorDetails), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.PARTIAL_CONTENT.value())
                .withMessage("Partial content found.")
                .withErrorDetails(ErrorDetail.builder().withMessage("Line Item invalid.").build())
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void calculateInvalidModelExceptionShouldThrowBadRequest() {
        List<com.sap.slh.tax.maestro.tax.exceptions.calculate.ErrorDetail> errorDetails = Arrays.asList(
                new com.sap.slh.tax.maestro.tax.exceptions.calculate.ErrorDetail(
                        TaxCalculationErrorMessage.DATE_INVALID, "Date invalid."),
                new com.sap.slh.tax.maestro.tax.exceptions.calculate.ErrorDetail(
                        TaxCalculationErrorMessage.ERROR_DOCUMENT_ID_REQUIRED, "Document ID cannot be blank."));

        ResponseEntity<ErrorResponse> error = handler.handleCalculateInvalidModelError(
                new CalculateInvalidModelException("Bad request.", errorDetails), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withMessage("The format of your request is invalid, review your request.")
                .withErrorDetails(ErrorDetail.builder().withMessage("Date invalid.").build())
                .build();

        ResponseEntity<ErrorResponse> expectedError = new ResponseEntity<>(expected, HttpStatus.BAD_REQUEST);

        assertEquals(expectedError, error);
    }

    @Test
    public void calculateInvalidModelExceptionShouldThrowInternalServerError() {
        List<com.sap.slh.tax.maestro.tax.exceptions.calculate.ErrorDetail> errorDetails = Arrays.asList(
                new com.sap.slh.tax.maestro.tax.exceptions.calculate.ErrorDetail(
                        TaxCalculationErrorMessage.ITEM_ID_EXISTS, "Item Id already exists in the document."),
                new com.sap.slh.tax.maestro.tax.exceptions.calculate.ErrorDetail(
                        TaxCalculationErrorMessage.TAX_ID_EXISTS,
                        "Tax Id already exists for the Item in the document."));

        ResponseEntity<ErrorResponse> error = handler.handleCalculateInvalidModelError(
                new CalculateInvalidModelException("Bad request.", errorDetails), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
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
                .withMessage("The JSON file of your payload request is too large. Reduce its size and send  the request again. For more information see Protection Against Denial-of-Service (DoS) Attacks.")
                .build();

        assertEquals(expected, error);
    }
}
