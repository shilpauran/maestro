package com.sap.slh.tax.maestro.tax.controller.handler;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.sap.slh.tax.maestro.api.v0.domain.ErrorType;
import com.sap.slh.tax.maestro.api.v0.schema.ErrorResponse;
import com.sap.slh.tax.maestro.tax.controller.MessageSourceMock;
import com.sap.slh.tax.maestro.tax.exceptions.partner.BadRequestFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.FailedRequestFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.GatewayTimeoutFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.NotFoundFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.PartnerResponseTooLargeException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.RequestTimeoutFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.ServiceUnavailableFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.UnauthorizedFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.UnsupportedTokenTypeException;

public class PartnerResponseErrorV0HandlerTest {

    private static PartnerResponseErrorV0Handler handler;
    private static final String DESTINATION_NAME = "VERTEX-CA";

    @Before
    public void setup() {
        MessageSourceMock msgSource = new MessageSourceMock();
        handler = new PartnerResponseErrorV0Handler(msgSource);
    }

    @Test
    public void testUnauthorizedFailedHttpResponseException() {
        UnauthorizedFailedHttpResponseException exception = new UnauthorizedFailedHttpResponseException(
                DESTINATION_NAME);

        ErrorResponse error = handler.handlePartnerUnauthorizedHttpResponseException(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.UNAUTHORIZED.value())
                .withType(ErrorType.PARTNER_DID_NOT_RESOLVE_TAXES)
                .withMessage("The SAP tax partner configured in the 'VERTEX-CA' destination did not authorize "
                        + "the connection from the tax service. Verify the configurations for Destinations in SAP "
                        + "Cloud Platform.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void testNotFoundFailedHttpResponseException() {
        NotFoundFailedHttpResponseException exception = new NotFoundFailedHttpResponseException(DESTINATION_NAME);

        ErrorResponse error = handler.handlePartnerNotFoundHttpResponseException(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.NOT_FOUND.value())
                .withType(ErrorType.PARTNER_DID_NOT_RESOLVE_TAXES)
                .withMessage("The SAP tax partner configured in destination 'VERTEX-CA' "
                        + "is not found. Verify the configurations for Destinations in SAP Cloud Platform.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void testBadRequestFailedHttpResponseException() {
        String msg = "Message from partner";
        BadRequestFailedHttpResponseException exception = new BadRequestFailedHttpResponseException("host", msg);

        ErrorResponse error = handler.handlePartnerBadRequestHttpResponseException(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage(msg)
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void testFailedRequestFailedHttpResponseException() {
        FailedRequestFailedHttpResponseException exception = new FailedRequestFailedHttpResponseException(
                DESTINATION_NAME);

        ErrorResponse error = handler.handlePartnerFailedRequestHttpResponseException(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.PARTNER_DID_NOT_RESOLVE_TAXES)
                .withMessage("Tax Service cannot process the payload request due to an unexpected error. "
                        + "This error is returned by the SAP tax partner you configured in the 'VERTEX-CA' "
                        + "destination. Contact your SAP tax partner.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void testGatewayTimeoutFailedHttpResponseException() {
        GatewayTimeoutFailedHttpResponseException exception = new GatewayTimeoutFailedHttpResponseException(
                DESTINATION_NAME);

        ErrorResponse error = handler.handleGatewayTimeoutFailedHttpResponseException(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.GATEWAY_TIMEOUT.value())
                .withType(ErrorType.PARTNER_DID_NOT_RESOLVE_TAXES)
                .withMessage("The payload request tax service sent to the SAP tax partner configured in the "
                        + "'VERTEX-CA' destination has timed out by the gateway. Contact your SAP tax partner.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void testRequestTimeoutFailedHttpResponseException() {
        RequestTimeoutFailedHttpResponseException exception = new RequestTimeoutFailedHttpResponseException(
                DESTINATION_NAME);

        ErrorResponse error = handler.handleRequestTimeoutFailedHttpResponseException(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.REQUEST_TIMEOUT.value())
                .withType(ErrorType.PARTNER_DID_NOT_RESOLVE_TAXES)
                .withMessage("The payload request tax service sent to the SAP tax partner configured in "
                        + "the 'VERTEX-CA' destination has timed out. Contact your SAP tax partner.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void testServiceUnavailableFailedHttpResponseException() {
        ServiceUnavailableFailedHttpResponseException exception = new ServiceUnavailableFailedHttpResponseException(
                DESTINATION_NAME);

        ErrorResponse error = handler.handleServiceUnavailableFailedHttpResponseException(exception, Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                .withType(ErrorType.PARTNER_DID_NOT_RESOLVE_TAXES)
                .withMessage("The SAP tax partner configured in destination 'VERTEX-CA' is unavailable "
                        + "or cannot process the payload request due to a high load. Contact your SAP tax partner.")
                .build();

        assertEquals(expected, error);
    }

    @Test
    public void unsupportedTokenTypeException() {
        ErrorResponse error = handler.handleUnsupportedTokenTypeException(
                new UnsupportedTokenTypeException("HOBA", "tax-engine-mock"), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withType(ErrorType.SERVER_EXCEPTION)
                .withMessage("An unexpected error occurred while processing your request. Open a support request.")
                .build();

        assertEquals(expected, error);

    }

    @Test
    public void responseTooLargeException() {
        ErrorResponse error = handler.handlePartnerResponseTooLargeException(
                new PartnerResponseTooLargeException(DESTINATION_NAME), Locale.US);

        ErrorResponse expected = ErrorResponse.builder()
                .withStatus(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .withType(ErrorType.INVALID_INPUT)
                .withMessage(
                        "SAP tax partner payload response is too large for the tax service to process. The payload response is returned by the SAP tax partner configured in the 'VERTEX-CA' destination. There are two possible causes for this problem: 1. Your payload request is too large. 2. There is an error in the SAP tax partner. Review your payload request and if the error persists, contact your SAP tax partner.")
                .build();

        assertEquals(expected, error);
    }

}
