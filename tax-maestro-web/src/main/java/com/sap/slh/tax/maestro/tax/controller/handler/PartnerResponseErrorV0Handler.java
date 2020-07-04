package com.sap.slh.tax.maestro.tax.controller.handler;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.sap.slh.tax.maestro.api.v0.domain.ErrorType;
import com.sap.slh.tax.maestro.api.v0.schema.ErrorResponse;
import com.sap.slh.tax.maestro.i18n.I18N;
import com.sap.slh.tax.maestro.tax.controller.TaxV0Controller;
import com.sap.slh.tax.maestro.tax.exceptions.partner.BadRequestFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.FailedRequestFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.GatewayTimeoutFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.NotFoundFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.PartnerResponseTooLargeException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.RequestTimeoutFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.ServiceUnavailableFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.UnauthorizedFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.UnsupportedTokenTypeException;

@ControllerAdvice(assignableTypes = {TaxV0Controller.class})
public class PartnerResponseErrorV0Handler {

    private static final Logger logger = LoggerFactory.getLogger(PartnerResponseErrorV0Handler.class);
    private MessageHelper msgHelper;

    public PartnerResponseErrorV0Handler(MessageSource msgSource) {
        this.msgHelper = new MessageHelper(msgSource);
    }

    @ExceptionHandler(UnauthorizedFailedHttpResponseException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handlePartnerUnauthorizedHttpResponseException(UnauthorizedFailedHttpResponseException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.UNAUTHORIZED.value())
                .withType(ErrorType.PARTNER_DID_NOT_RESOLVE_TAXES)
                .withMessage(message(I18N.MESSAGE_PARTNER_UNAUTHORIZED_HTTP_RESPONSE, ex.getAttributesAsArgs(), locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @ExceptionHandler(NotFoundFailedHttpResponseException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handlePartnerNotFoundHttpResponseException(NotFoundFailedHttpResponseException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder().withStatus(HttpStatus.NOT_FOUND.value())
                .withType(ErrorType.PARTNER_DID_NOT_RESOLVE_TAXES)
                .withMessage(message(I18N.MESSAGE_PARTNER_NOT_FOUND_HTTP_RESPONSE, ex
                        .getAttributesAsArgs(), locale)).build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @ExceptionHandler(BadRequestFailedHttpResponseException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handlePartnerBadRequestHttpResponseException(BadRequestFailedHttpResponseException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder().withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage(ex.getMessage()).build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @ExceptionHandler(FailedRequestFailedHttpResponseException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handlePartnerFailedRequestHttpResponseException(FailedRequestFailedHttpResponseException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.PARTNER_DID_NOT_RESOLVE_TAXES)
                .withMessage(message(I18N.MESSAGE_PARTNER_SERVER_ERROR, ex.getAttributesAsArgs(), locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @ExceptionHandler(GatewayTimeoutFailedHttpResponseException.class)
    @ResponseStatus(value = HttpStatus.GATEWAY_TIMEOUT)
    @ResponseBody
    public ErrorResponse handleGatewayTimeoutFailedHttpResponseException(GatewayTimeoutFailedHttpResponseException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.GATEWAY_TIMEOUT.value())
                .withType(ErrorType.PARTNER_DID_NOT_RESOLVE_TAXES)
                .withMessage(message(I18N.MESSAGE_PARTNER_GATEWAY_TIMEOUT, ex.getAttributesAsArgs(), locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @ExceptionHandler(RequestTimeoutFailedHttpResponseException.class)
    @ResponseStatus(value = HttpStatus.REQUEST_TIMEOUT)
    @ResponseBody
    public ErrorResponse handleRequestTimeoutFailedHttpResponseException(RequestTimeoutFailedHttpResponseException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.REQUEST_TIMEOUT.value())
                .withType(ErrorType.PARTNER_DID_NOT_RESOLVE_TAXES)
                .withMessage(message(I18N.MESSAGE_PARTNER_REQUEST_TIMEOUT, ex.getAttributesAsArgs(), locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    @ExceptionHandler(ServiceUnavailableFailedHttpResponseException.class)
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    public ErrorResponse handleServiceUnavailableFailedHttpResponseException(ServiceUnavailableFailedHttpResponseException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                .withType(ErrorType.PARTNER_DID_NOT_RESOLVE_TAXES)
                .withMessage(message(I18N.MESSAGE_PARTNER_UNAVAILABLE, ex.getAttributesAsArgs(), locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }
    
    @ExceptionHandler(UnsupportedTokenTypeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleUnsupportedTokenTypeException(UnsupportedTokenTypeException ex, Locale locale) {
  
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.SERVER_EXCEPTION)
                .withMessage(message(I18N.MESSAGE_UNEXPECTED_EXCEPTION, null, locale))
                .build();

        this.logException(errorResponse, ex);
        return errorResponse;        
        
    }
    
    @ExceptionHandler(PartnerResponseTooLargeException.class)
    @ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
    @ResponseBody
    public ErrorResponse handlePartnerResponseTooLargeException(PartnerResponseTooLargeException ex, Locale locale) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .withStatus(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage(message(I18N.MESSAGE_PARTNER_RESPONSE_TOO_LARGE, ex.getAttributesAsArgs(), locale))
                .build();
        this.logException(errorResponse, ex);
        return errorResponse;
    }

    private void logException(ErrorResponse errorResponse, Exception ex) {
        if (logger.isErrorEnabled()) {
            logger.error(errorResponse.getMessage(), ex);
        }
    }

    private String message(I18N code, @Nullable Object[] args, Locale locale) {
        return this.msgHelper.message(code, args, locale);
    }
}
