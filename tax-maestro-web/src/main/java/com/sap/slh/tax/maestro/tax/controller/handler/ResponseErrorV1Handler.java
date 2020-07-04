package com.sap.slh.tax.maestro.tax.controller.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.sap.slh.tax.calculation.model.common.TaxCalculationErrorMessage;
import com.sap.slh.tax.maestro.api.common.exception.InvalidModelException;
import com.sap.slh.tax.maestro.api.v1.schema.ErrorDetail;
import com.sap.slh.tax.maestro.api.v1.schema.ErrorResponse;
import com.sap.slh.tax.maestro.context.tenant.TenantNotRetrievedException;
import com.sap.slh.tax.maestro.i18n.I18N;
import com.sap.slh.tax.maestro.tax.controller.TaxV1Controller;
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
import com.sap.slh.tax.maestro.tax.exceptions.enhance.EnhanceBadRequestException;

@ControllerAdvice(assignableTypes = { TaxV1Controller.class })
public class ResponseErrorV1Handler extends ResponseErrorAbstractHandler<ErrorResponse> {
    private static final Logger logger = LoggerFactory.getLogger(ResponseErrorV1Handler.class);

    /**
     * Control advice class for {@link TaxV1Controller}.
     *
     * @param msgSource
     *            {@link MessageSource} used to localize error messages.
     */
    public ResponseErrorV1Handler(MessageSource msgSource) {
        super(msgSource);
    }

    @Override
    public ErrorResponse handleTenantNotRetrievedException(TenantNotRetrievedException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withMessage(message(I18N.MESSAGE_TENANT_NOT_RETRIEVED, null, locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleJMXException(JMXException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withMessage(message(I18N.MESSAGE_UNEXPECTED_EXCEPTION, null, locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleNoAmqpResponseException(NoAmqpResponseException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withMessage(message(I18N.MESSAGE_UNEXPECTED_EXCEPTION, null, locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }    
    
    @Override
    public ErrorResponse handleUnexpectedTaxException(TaxMaestroException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withMessage(message(I18N.MESSAGE_UNEXPECTED_EXCEPTION, null, locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleMandatoryAttributesMissingError(InvalidModelException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withMessage(message(I18N.MESSAGE_INVALID_REQUEST_EXCEPTION, null, locale))
                .withErrorDetails(this.getErrorDetailsFromMessageList(
                        formatter().getPropertyErrorsMessages(ex.getPropertyErrors(), locale)))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleDecodingError(DecodingException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withMessage(ex.getMessage())
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleInvalidJson(Exception ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
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
                .withMessage(message(error.messageCode(), messageParameters.toArray(), locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleUnexpectedException(Exception ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withMessage(message(I18N.MESSAGE_UNEXPECTED_EXCEPTION, null, locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleQueueCommunicationException(QueueCommunicationException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withMessage(message(I18N.MESSAGE_UNEXPECTED_EXCEPTION, null, locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleUnknowAmqpResponseType(UnknownAmqpResponseTypeException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
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
                .withMessage(message(I18N.MESSAGE_NO_RELEVANT_COUNTRY_FOR_DESTINATION, null, locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleJwtNotProvided(NoJwtProvidedException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withMessage(message(I18N.MESSAGE_JWT_NOT_PROVIDED, null, locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleDetermineNoContentError(DetermineNoContentException ex, Locale locale) {
        List<ErrorDetail> errorDetails = getErrorDetailsDetermineContentError(ex.getErrorDetails());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.NO_CONTENT.value())
                .withMessage(ex.getMessage())
                .withErrorDetails(CollectionUtils.isEmpty(errorDetails) ? null : errorDetails)
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleDeterminePartialContentError(DeterminePartialContentException ex, Locale locale) {
        List<ErrorDetail> errorDetails = getErrorDetailsDetermineContentError(ex.getErrorDetails());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.PARTIAL_CONTENT.value())
                .withMessage(ex.getMessage())
                .withErrorDetails(CollectionUtils.isEmpty(errorDetails) ? null : errorDetails)
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ResponseEntity<ErrorResponse> handleDetermineInvalidModelError(DetermineInvalidModelException ex,
            Locale locale) {
        List<ErrorDetail> errorDetails = ex.getErrorDetails()
                .stream()
                .map(detErrorDetails -> ErrorDetail.builder().withMessage(detErrorDetails.getMessage()).build())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withMessage(message(I18N.MESSAGE_INVALID_REQUEST_EXCEPTION, null, locale))
                .withErrorDetails(errorDetails)
                .build();

        this.logException(errorResponse, ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EnhanceBadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleEnhanceBadRequestError(EnhanceBadRequestException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withMessage(ex.getMessage())
                .withErrorDetails(getErrorDetailsFromMessageList(ex.getErrorDetails()))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleCalculateNoContentError(CalculateNoContentException ex, Locale locale) {
        List<ErrorDetail> errorDetails = getErrorDetailsCalculateContentError(ex.getErrorDetails());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.NO_CONTENT.value())
                .withMessage(ex.getMessage())
                .withErrorDetails(CollectionUtils.isEmpty(errorDetails) ? null : errorDetails)
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ErrorResponse handleCalculatePartialContentError(CalculatePartialContentException ex, Locale locale) {
        List<ErrorDetail> errorDetails = getErrorDetailsCalculateContentError(ex.getErrorDetails());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.PARTIAL_CONTENT.value())
                .withMessage(ex.getMessage())
                .withErrorDetails(CollectionUtils.isEmpty(errorDetails) ? null : errorDetails)
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @Override
    public ResponseEntity<ErrorResponse> handleCalculateInvalidModelError(CalculateInvalidModelException ex,
            Locale locale) {
        String message = message(I18N.MESSAGE_INVALID_REQUEST_EXCEPTION, null, locale);
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        List<ErrorDetail> errorDetails = getErrorDetailsCalculateInvalidModel(ex.getErrorDetails());

        if (CollectionUtils.isEmpty(errorDetails)) {
            message = message(I18N.MESSAGE_UNEXPECTED_EXCEPTION, null, locale);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            errorDetails = null;
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(httpStatus.value())
                .withMessage(message)
                .withErrorDetails(errorDetails)
                .build();

        this.logException(errorResponse, ex);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }
    
    @Override
    public ErrorResponse handleDataBufferLimitException(DataBufferLimitException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.PAYLOAD_TOO_LARGE.value())
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

    private List<ErrorDetail> getErrorDetailsFromMessageList(List<String> messages) {
        List<ErrorDetail> errorDetails = new ArrayList<>();
        for (String message : messages) {
            errorDetails.add(ErrorDetail.builder().withMessage(message).build());
        }
        return errorDetails;
    }

    private List<ErrorDetail> getErrorDetailsCalculateInvalidModel(
            List<com.sap.slh.tax.maestro.tax.exceptions.calculate.ErrorDetail> errorDetails) {
        Set<TaxCalculationErrorMessage> badRequestExceptionCodes = getCalculateBadRequestExceptionCodes();

        return errorDetails.stream()
                .filter(err -> badRequestExceptionCodes.contains(err.getErrorCode()))
                .map(detErrorDetails -> ErrorDetail.builder().withMessage(detErrorDetails.getMessage()).build())
                .collect(Collectors.toList());
    }

    private List<ErrorDetail> getErrorDetailsCalculateContentError(
            List<com.sap.slh.tax.maestro.tax.exceptions.calculate.ErrorDetail> calcErrorDetails) {
        List<String> detailsMessage = calcErrorDetails.stream()
                .map(com.sap.slh.tax.maestro.tax.exceptions.calculate.ErrorDetail::getMessage)
                .collect(Collectors.toList());

        return getErrorDetailsFromMessageList(detailsMessage);
    }
    
    private List<ErrorDetail> getErrorDetailsDetermineContentError(
            List<com.sap.slh.tax.maestro.tax.exceptions.determine.ErrorDetail> detErrorDetails) {
        List<String> detailsMessage = detErrorDetails.stream()
                .map(com.sap.slh.tax.maestro.tax.exceptions.determine.ErrorDetail::getMessage)
                .collect(Collectors.toList());

        return getErrorDetailsFromMessageList(detailsMessage);
    }
}
