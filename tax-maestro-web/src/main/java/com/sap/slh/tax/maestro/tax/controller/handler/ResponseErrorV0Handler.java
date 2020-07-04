package com.sap.slh.tax.maestro.tax.controller.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.sap.slh.tax.attributes.determination.model.api.ValidationErrorCode;
import com.sap.slh.tax.calculation.model.common.TaxCalculationErrorMessage;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.v0.domain.ErrorType;
import com.sap.slh.tax.maestro.api.v0.schema.ErrorResponse;
import com.sap.slh.tax.maestro.context.tenant.TenantNotRetrievedException;
import com.sap.slh.tax.maestro.i18n.I18N;
import com.sap.slh.tax.maestro.tax.controller.TaxV0Controller;
import com.sap.slh.tax.maestro.tax.exceptions.DestinationBadRequestException;
import com.sap.slh.tax.maestro.tax.exceptions.DirectPayloadNotSupportedException;
import com.sap.slh.tax.maestro.tax.exceptions.JMXException;
import com.sap.slh.tax.maestro.tax.exceptions.NoAmqpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.NoJwtProvidedException;
import com.sap.slh.tax.maestro.tax.exceptions.NoRelevantCountryForDestinationException;
import com.sap.slh.tax.maestro.tax.exceptions.QueueCommunicationException;
import com.sap.slh.tax.maestro.tax.exceptions.TaxMaestroException;
import com.sap.slh.tax.maestro.tax.exceptions.UnknownAmqpResponseTypeException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.CalculateInvalidModelException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.CalculateNoContentException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.CalculatePartialContentException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.DetermineInvalidModelException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.DetermineNoContentException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.DeterminePartialContentException;
import com.sap.slh.tax.maestro.tax.exceptions.determine.ErrorDetail;

@ControllerAdvice(assignableTypes = { TaxV0Controller.class })
public class ResponseErrorV0Handler extends ResponseErrorAbstractHandler<ErrorResponse> {
    private static final Logger logger = LoggerFactory.getLogger(ResponseErrorV0Handler.class);

    /**
     * Control advice class for {@link TaxV0Controller}.
     *
     * @param msgSource
     *            {@link MessageSource} used to localize error messages.
     */
    public ResponseErrorV0Handler(MessageSource msgSource) {
        super(msgSource);
    }

    @Override
    public ErrorResponse handleTenantNotRetrievedException(TenantNotRetrievedException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.SERVER_EXCEPTION)
                .withMessage(message(I18N.MESSAGE_TENANT_NOT_RETRIEVED, null, locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleNoAmqpResponseException(NoAmqpResponseException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.SERVER_EXCEPTION)
                .withMessage(message(I18N.MESSAGE_UNEXPECTED_EXCEPTION, null, locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleJMXException(JMXException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.SERVER_EXCEPTION)
                .withMessage(message(I18N.MESSAGE_UNEXPECTED_EXCEPTION, null, locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }    
    
    @Override
    public ErrorResponse handleUnexpectedTaxException(TaxMaestroException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.SERVER_EXCEPTION)
                .withMessage(message(I18N.MESSAGE_UNEXPECTED_EXCEPTION, null, locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleMandatoryAttributesMissingError(InvalidModelException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage(formatter().getPropertyErrorsSingleMessage(ex.getPropertyErrors(), locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleDecodingError(DecodingException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.BAD_REQUEST)
                .withMessage(ex.getMessage())
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleInvalidJson(Exception ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage(message(I18N.MESSAGE_INVALID_JSON_REQUEST, null, locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleInvalidFormatError(MismatchedInputException ex, Locale locale) {
        List<Object> messageParameters = new ArrayList<>();

        MismatchedInputExceptionError error = MismatchedInputExceptionError.from(ex);
        messageParameters.add(error.mismatchedValue());
        messageParameters.add(error.jsonPath());
        messageParameters.add(error.expectedValue());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage(message(error.messageCode(), messageParameters.toArray(), locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleUnexpectedException(Exception ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.SERVER_EXCEPTION)
                .withMessage(message(I18N.MESSAGE_UNEXPECTED_EXCEPTION, null, locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleQueueCommunicationException(QueueCommunicationException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.SERVER_EXCEPTION)
                .withMessage(message(I18N.MESSAGE_UNEXPECTED_EXCEPTION, null, locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleUnknowAmqpResponseType(UnknownAmqpResponseTypeException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.SERVER_EXCEPTION)
                .withMessage(message(I18N.MESSAGE_UNEXPECTED_EXCEPTION, null, locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleNoRelevantCountryForDestination(NoRelevantCountryForDestinationException ex,
            Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage(message(I18N.MESSAGE_NO_RELEVANT_COUNTRY_FOR_DESTINATION, null, locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleJwtNotProvided(NoJwtProvidedException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.BAD_REQUEST)
                .withMessage(message(I18N.MESSAGE_JWT_NOT_PROVIDED, null, locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @ExceptionHandler(DirectPayloadNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleDirectPayloadNotSupportedException(DirectPayloadNotSupportedException ex,
            Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage(message(I18N.MESSAGE_DIRECT_PAYLOAD_NOT_SUPPORTED, null, locale))
                .build();

        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleDetermineNoContentError(DetermineNoContentException ex, Locale locale) {
        String message = getMessageDetermineContentError(ex.getMessage(), ex.getErrorDetails());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.NO_CONTENT.value())
                .withType(ErrorType.NO_CONTENT)
                .withMessage(message)
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleDeterminePartialContentError(DeterminePartialContentException ex, Locale locale) {
        String message = getMessageDetermineContentError(ex.getMessage(), ex.getErrorDetails());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.PARTIAL_CONTENT.value())
                .withType(ErrorType.PARTIAL_CONTENT)
                .withMessage(message)
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ResponseEntity<ErrorResponse> handleDetermineInvalidModelError(DetermineInvalidModelException ex,
            Locale locale) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ErrorType responseErrorType = ErrorType.INVALID_INPUT;

        String detailsMessage = getDetailsMessageDetermineInvalidModelError(ex.getErrorDetails());
        String message;

        if (StringUtils.isEmpty(detailsMessage)) {
            message = message(I18N.MESSAGE_UNEXPECTED_EXCEPTION, null, locale);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseErrorType = ErrorType.SERVER_EXCEPTION;
        } else {
            message = buildfinalMessageIntegrationErrors(ex.getMessage(), detailsMessage);
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(httpStatus.value())
                .withType(responseErrorType)
                .withMessage(message)
                .build();

        this.logException(errorResponse, ex);
        return new ResponseEntity<>(errorResponse, httpStatus);

    }

    @ExceptionHandler(DestinationBadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleDestinationBadRequestError(DestinationBadRequestException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_CONTENT)
                .withMessage(ex.getMessage())
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleCalculateNoContentError(CalculateNoContentException ex, Locale locale) {
        String message = getMessageCalculateContentError(ex.getMessage(), ex.getErrorDetails());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.NO_CONTENT.value())
                .withType(ErrorType.NO_CONTENT)
                .withMessage(message)
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleCalculatePartialContentError(CalculatePartialContentException ex, Locale locale) {
        String message = getMessageCalculateContentError(ex.getMessage(), ex.getErrorDetails());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.PARTIAL_CONTENT.value())
                .withType(ErrorType.PARTIAL_CONTENT)
                .withMessage(message)
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ResponseEntity<ErrorResponse> handleCalculateInvalidModelError(CalculateInvalidModelException ex,
            Locale locale) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ErrorType responseErrorType = ErrorType.INVALID_INPUT;

        String detailsMessage = getDetailsMessageCalculateInvalidModelError(ex.getErrorDetails());
        String message;

        if (StringUtils.isEmpty(detailsMessage)) {
            message = message(I18N.MESSAGE_UNEXPECTED_EXCEPTION, null, locale);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            responseErrorType = ErrorType.SERVER_EXCEPTION;
        } else {
            message = buildfinalMessageIntegrationErrors(ex.getMessage(), detailsMessage);
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(httpStatus.value())
                .withType(responseErrorType)
                .withMessage(message)
                .build();

        this.logException(errorResponse, ex);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }
    
    @Override
    public ErrorResponse handleDataBufferLimitException(DataBufferLimitException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage(message(I18N.MESSAGE_PAYLOAD_TOO_LARGE, null, locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    private void logException(ErrorResponse errorResponse, Exception ex) {
        if (logger.isErrorEnabled()) {
            logger.error(errorResponse.getMessage(), ex);
        }
    }

    private String getDetailsMessageDetermineInvalidModelError(List<ErrorDetail> errorDetails) {
        Set<ValidationErrorCode> internalServerExceptionCodes = getDetermineInternalServerExceptionCodes();

        String message;
        message = errorDetails.stream()
                .filter(err -> !internalServerExceptionCodes.contains(err.getErrorCode()))
                .map(ErrorDetail::getMessage)
                .collect(Collectors.joining(" "));

        return message;
    }

    private Set<ValidationErrorCode> getDetermineInternalServerExceptionCodes() {
        return Stream
                .of(ValidationErrorCode.INVALID_PRODUCT_ID, ValidationErrorCode.INVALID_PARTY_ID,
                        ValidationErrorCode.MISSING_ITEM_PRODUCT_ID, ValidationErrorCode.NON_UNIQUE_PRODUCT,
                        ValidationErrorCode.NON_UNIQUE_PARTY, ValidationErrorCode.ERROR_DOCUMENT_PRODUCT_REQUIRED,
                        ValidationErrorCode.ERROR_DOCUMENT_COMPANYINFO_REQUIRED,
                        ValidationErrorCode.ERROR_ITEM_PARTY_DETAILS_REQUIRED,
                        ValidationErrorCode.ERROR_ITEM_PARTY_DETAILS_ID_REQUIRED,
                        ValidationErrorCode.ERROR_PRODUCT_ID_REQUIRED, ValidationErrorCode.ERROR_PARTY_ID_REQUIRED)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private String getDetailsMessageCalculateInvalidModelError(
            List<com.sap.slh.tax.maestro.tax.exceptions.calculate.ErrorDetail> errorDetails) {
        Set<TaxCalculationErrorMessage> badRequestExceptionCodes = getCalculateBadRequestExceptionCodes();

        String message;
        message = errorDetails.stream()
                .filter(err -> badRequestExceptionCodes.contains(err.getErrorCode()))
                .map(com.sap.slh.tax.maestro.tax.exceptions.calculate.ErrorDetail::getMessage)
                .collect(Collectors.joining(" "));

        return message;
    }

    private String getMessageCalculateContentError(String message,
            List<com.sap.slh.tax.maestro.tax.exceptions.calculate.ErrorDetail> errorDetails) {
        String detailsMessage = errorDetails.stream()
                .map(com.sap.slh.tax.maestro.tax.exceptions.calculate.ErrorDetail::getMessage)
                .collect(Collectors.joining(" "));

        return buildfinalMessageIntegrationErrors(message, detailsMessage);
    }
    
    private String getMessageDetermineContentError(String message,
            List<ErrorDetail> errorDetails) {
        String detailsMessage = errorDetails.stream()
                .map(ErrorDetail::getMessage)
                .collect(Collectors.joining(" "));

        return buildfinalMessageIntegrationErrors(message, detailsMessage);
    }

    private String buildfinalMessageIntegrationErrors(String message, String detailsMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append(message);
        sb.append(" ");

        sb.append(detailsMessage);

        return sb.toString().trim();
    }
}
