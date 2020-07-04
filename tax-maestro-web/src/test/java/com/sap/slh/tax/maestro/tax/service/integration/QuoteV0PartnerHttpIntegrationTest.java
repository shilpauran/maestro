package com.sap.slh.tax.maestro.tax.service.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationAuthorization;
import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationResponse;
import com.sap.slh.tax.destinationconfiguration.destinations.models.TokenTypeCode;
import com.sap.slh.tax.maestro.api.common.domain.CountryCode;
import com.sap.slh.tax.maestro.api.common.domain.CurrencyCode;
import com.sap.slh.tax.maestro.api.v0.domain.ErrorType;
import com.sap.slh.tax.maestro.api.v0.domain.GrossOrNet;
import com.sap.slh.tax.maestro.api.v0.domain.ItemType;
import com.sap.slh.tax.maestro.api.v0.domain.LocationType;
import com.sap.slh.tax.maestro.api.v0.domain.SaleOrPurchase;
import com.sap.slh.tax.maestro.api.v0.schema.ErrorResponse;
import com.sap.slh.tax.maestro.api.v0.schema.ExemptionDetail;
import com.sap.slh.tax.maestro.api.v0.schema.Item;
import com.sap.slh.tax.maestro.api.v0.schema.Location;
import com.sap.slh.tax.maestro.api.v0.schema.TaxRequest;
import com.sap.slh.tax.maestro.api.v0.schema.TaxResponse;
import com.sap.slh.tax.maestro.context.RequestContextService;
import com.sap.slh.tax.maestro.context.correlation.CorrelationIdHeaderService;
import com.sap.slh.tax.maestro.tax.exceptions.partner.BadRequestFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.FailedRequestFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.GatewayTimeoutFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.NotFoundFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.PartnerResponseTooLargeException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.RequestTimeoutFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.ServiceUnavailableFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.UnauthorizedFailedHttpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.partner.UnsupportedTokenTypeException;
import com.sap.slh.tax.maestro.tax.jmx.QuoteHttpIntegrationMBeansHandler;
import com.sap.slh.tax.maestro.tax.jmx.QuoteHttpInternalServerError;
import com.sap.slh.tax.maestro.tax.jmx.QuoteHttpUnavailabilityError;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class QuoteV0PartnerHttpIntegrationTest {

    private static final String URL = "http://taas.taxweb.com.br:9090/taxgateway/webapi/taxrules/calctaxessap";
    private static final String HOST = "taas.taxweb.com.br";
    private static final String HTTP_HEADER_BASIC_AUTHORIZATION = "Basic 21423452345234523465546546";
    private static final String HTTP_HEADER_BEARER_AUTHORIZATION = "Bearer 21423452345234523465546546";
    private static final String HTTP_HEADER_CACHE_CONTROL = "no-cache, no-store, max-age=0, must-revalidate";
    private static final String TOKEN = "21423452345234523465546546";
    private static final String DEFAULT_DESTINATION = "TAXWEB-BR";
    private static final String DEFAULT_TENANT = "tenant1";
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    private static final String DEFAULT_CORRELATION_ID = "b03d17e7-480d-4f3b-9f3c-5307a0d27321";

    private WebClient webClientMock;
    private WebClient.RequestHeadersSpec requestHeadersMock;
    private WebClient.RequestBodySpec requestBodyMock;
    private WebClient.RequestBodyUriSpec requestBodyUriMock;
    private ClientResponse responseMock;

    private RequestContextService requestContextService;

    private QuoteV0PartnerHttpIntegration integration;

    private TaxRequest request = getDefaultRequest();
    private TaxResponse response = TaxResponse.builder().withPartnerName("TEST").build();

    @Before
    public void setup() {
        webClientMock = Mockito.mock(WebClient.class);
        requestHeadersMock = Mockito.mock(WebClient.RequestHeadersSpec.class);
        requestBodyMock = Mockito.mock(WebClient.RequestBodySpec.class);
        requestBodyUriMock = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        responseMock = Mockito.mock(ClientResponse.class);

        requestContextService = Mockito.mock(RequestContextService.class);
        Mockito.when(requestContextService.getCorrelationId()).thenReturn(Mono.empty());
        Mockito.when(requestContextService.getTenantId()).thenReturn(Mono.just(DEFAULT_TENANT));
        Mockito.when(requestContextService.getLocale()).thenReturn(Mono.just(DEFAULT_LOCALE));
        Mockito.when(requestContextService.getCacheControl()).thenReturn(Mono.empty());

        integration = new QuoteV0PartnerHttpIntegration(webClientMock, requestContextService);
        integration.quoteMBeansHandler = Mockito.mock(QuoteHttpIntegrationMBeansHandler.class);

    }

    @Test
    public void testCallQuoteHttpIntegrationWithCacheControl() throws MalformedURLException {
        Mockito.when(requestContextService.getCacheControl()).thenReturn(Mono.just(HTTP_HEADER_CACHE_CONTROL));
        Mockito.when(requestContextService.getCorrelationId()).thenReturn(Mono.just(DEFAULT_CORRELATION_ID));
        this.mockHttpResponse(URL, HttpStatus.OK, this.response);

        StepVerifier
                .create(this.integration.call(getDestinationResponseWithoutAuthentication(), Mono.just(this.request)))
                .consumeNextWith((output -> {
                    assertEquals(this.response, output);
                    verifyCallTimesHTTPHeaders(0, 0, 1, 1);
                })).verifyComplete();
    }

    @Test
    public void testCallQuoteHttpIntegrationWithoutAuthentication() throws MalformedURLException {

        this.mockHttpResponse(URL, HttpStatus.OK, this.response);

        DestinationResponse destinationResponse = getDestinationResponseWithoutAuthentication();

        StepVerifier.create(this.integration.call(destinationResponse, Mono.just(this.request)))
                .consumeNextWith((output -> {
                    assertEquals(this.response, output);
                    verifyCallTimesHTTPHeaders(0, 0, 0, 0);
                })).verifyComplete();
    }

    @Test
    public void testCallQuoteHttpIntegrationWithNoneAuthentication() throws MalformedURLException {

        this.mockHttpResponse(URL, HttpStatus.OK, this.response);

        StepVerifier.create(this.integration.call(getDestinationResponse(new URL(URL), TokenTypeCode.NONE),
                Mono.just(this.request))).consumeNextWith((output -> {
                    assertEquals(this.response, output);
                    verifyCallTimesHTTPHeaders(0, 0, 0, 0);
                })).verifyComplete();
    }

    @Test
    public void testCallQuoteHttpIntegrationWithBasicAuthentication() throws MalformedURLException {

        this.mockHttpResponse(URL, HttpStatus.OK, this.response);

        StepVerifier.create(this.integration.call(getDestinationResponse(new URL(URL), TokenTypeCode.BASIC),
                Mono.just(this.request))).consumeNextWith((output -> {
                    assertEquals(this.response, output);
                    verifyCallTimesHTTPHeaders(1, 0, 0, 0);
                })).verifyComplete();
    }

    @Test
    public void testCallQuoteHttpIntegrationWithMoreThanOneHeader() throws MalformedURLException {
        Mockito.when(requestContextService.getCacheControl()).thenReturn(Mono.just(HTTP_HEADER_CACHE_CONTROL));
        Mockito.when(requestContextService.getCorrelationId()).thenReturn(Mono.just(DEFAULT_CORRELATION_ID));
        this.mockHttpResponse(URL, HttpStatus.OK, this.response);

        StepVerifier.create(this.integration.call(getDestinationResponse(new URL(URL), TokenTypeCode.BASIC),
                Mono.just(this.request))).consumeNextWith((output -> {
                    assertEquals(this.response, output);
                    verifyCallTimesHTTPHeaders(1, 0, 1, 1);
                })).verifyComplete();
    }

    @Test
    public void testCallQuoteHttpIntegrationWithBearerToken() throws MalformedURLException {

        this.mockHttpResponse(URL, HttpStatus.OK, response);

        StepVerifier.create(
                integration.call(getDestinationResponse(new URL(URL), TokenTypeCode.BEARER), Mono.just(request)))
                .consumeNextWith((output -> {
                    assertEquals(response, output);
                    verifyCallTimesHTTPHeaders(0, 1, 0, 0);
                })).verifyComplete();
    }

    @Test
    public void testCallQuoteHttpIntegrationUnauthorizedRequest() throws MalformedURLException {
        this.mockHttpResponse(URL, HttpStatus.UNAUTHORIZED, "");

        StepVerifier.create(this.integration.call(getDestinationResponse(new URL(URL), TokenTypeCode.BASIC),
                Mono.just(this.request))).expectError(UnauthorizedFailedHttpResponseException.class).verify();
    }

    @Test
    public void testCallQuoteHttpIntegrationWithUnsupportedToken() throws MalformedURLException {
        this.mockHttpResponse(URL, HttpStatus.INTERNAL_SERVER_ERROR, "");

        StepVerifier.create(this.integration.call(getDestinationResponse(new URL(URL), null), Mono.just(this.request)))
                .expectError(UnsupportedTokenTypeException.class).verify();
    }

    @Test
    public void testCallQuoteHttpIntegrationBadRequest() throws MalformedURLException {

        ErrorResponse errorResponse = ErrorResponse.builder().withType(ErrorType.INVALID_INPUT)
                .withMessage("Invalid Field A").build();

        this.mockHttpResponse(URL, HttpStatus.BAD_REQUEST, errorResponse);

        StepVerifier.create(this.integration.call(getDestinationResponse(new URL(URL), TokenTypeCode.BASIC),
                Mono.just(this.request))).consumeErrorWith(exception -> {
                    BadRequestFailedHttpResponseException error = (BadRequestFailedHttpResponseException) exception;
                    assertEquals("Invalid Field A", error.getMessage());
                }).verify();
    }

    @Test
    public void testConversionErrorResponseError() throws MalformedURLException {
        this.mockHttpResponse(URL, HttpStatus.BAD_REQUEST, response);

        StepVerifier.create(this.integration.call(getDestinationResponse(new URL(URL), TokenTypeCode.BASIC),
                Mono.just(this.request))).expectError(FailedRequestFailedHttpResponseException.class).verify();
    }

    @Test
    public void testCallQuoteHttpIntegrationNotFound() throws MalformedURLException {
        this.mockHttpResponse(URL, HttpStatus.NOT_FOUND, "");

        StepVerifier.create(this.integration.call(getDestinationResponse(new URL(URL), TokenTypeCode.BASIC),
                Mono.just(this.request))).expectError(NotFoundFailedHttpResponseException.class).verify();
    }

    @Test
    public void testCallQuoteHttpIntegrationBadRequestMalformed() throws MalformedURLException {

        ErrorResponse errorResponse = ErrorResponse.builder().withType(ErrorType.BAD_REQUEST)
                .withMessage("Invalid Field A").build();

        this.mockHttpResponse(URL, HttpStatus.BAD_REQUEST, errorResponse);

        StepVerifier.create(this.integration.call(getDestinationResponse(new URL(URL), TokenTypeCode.BASIC),
                Mono.just(this.request))).expectError(FailedRequestFailedHttpResponseException.class).verify();
    }

    @Test
    public void testCallQuoteHttpIntegrationServerException() throws MalformedURLException {
        this.mockHttpResponse(URL, HttpStatus.INTERNAL_SERVER_ERROR, this.response);

        QuoteHttpInternalServerError mBean = new QuoteHttpInternalServerError();
        Mockito.when(this.integration.quoteMBeansHandler.getInternalServerErrorMBeanByHost(HOST)).thenReturn(mBean);

        StepVerifier.create(this.integration.call(getDestinationResponse(new URL(URL), TokenTypeCode.BASIC),
                Mono.just(this.request))).consumeErrorWith(exception -> {
                    assertTrue(exception instanceof FailedRequestFailedHttpResponseException);
                    assertEquals(1, mBean.getCount());
                }).verify();
    }

    @Test
    public void testCallQuoteHttpIntegrationRequestTimeout() throws MalformedURLException {
        this.mockHttpResponse(URL, HttpStatus.REQUEST_TIMEOUT, "");

        StepVerifier.create(this.integration.call(getDestinationResponse(new URL(URL), TokenTypeCode.BASIC),
                Mono.just(this.request))).expectError(RequestTimeoutFailedHttpResponseException.class).verify();
    }

    @Test
    public void testCallQuoteHttpIntegrationServiceUnavailable() throws MalformedURLException {
        this.mockHttpResponse(URL, HttpStatus.SERVICE_UNAVAILABLE, "");

        QuoteHttpUnavailabilityError mBean = new QuoteHttpUnavailabilityError();
        Mockito.when(this.integration.quoteMBeansHandler.getUnavailabilityErrorMBeanByHost(HOST)).thenReturn(mBean);

        StepVerifier.create(this.integration.call(getDestinationResponse(new URL(URL), TokenTypeCode.BASIC),
                Mono.just(this.request))).consumeErrorWith(exception -> {
                    assertTrue(exception instanceof ServiceUnavailableFailedHttpResponseException);
                    assertEquals(1, mBean.getCount());
                }).verify();
    }

    @Test
    public void testCallQuoteHttpIntegrationGatewayTimeout() throws MalformedURLException {
        this.mockHttpResponse(URL, HttpStatus.GATEWAY_TIMEOUT, "");

        QuoteHttpUnavailabilityError mBean = new QuoteHttpUnavailabilityError();
        Mockito.when(this.integration.quoteMBeansHandler.getUnavailabilityErrorMBeanByHost(HOST)).thenReturn(mBean);

        StepVerifier.create(this.integration.call(getDestinationResponse(new URL(URL), TokenTypeCode.BASIC),
                Mono.just(this.request))).consumeErrorWith(exception -> {
                    assertTrue(exception instanceof GatewayTimeoutFailedHttpResponseException);
                    assertEquals(1, mBean.getCount());
                }).verify();
    }

    @Test
    public void testCallQuoteHttpIntegrationUnkownPartnerUrl() throws MalformedURLException {
        String url = "http://txspartner.dumy.jm";

        mockExchangeWithException(url, new UnknownHostException());

        StepVerifier.create(this.integration.call(getDestinationResponse(new URL(url), TokenTypeCode.BASIC),
                Mono.just(this.request))).expectError(NotFoundFailedHttpResponseException.class).verify();
    }

    @Test
    public void testCallQuoteHttpIntegrationPartnerUnavailable() throws MalformedURLException {
        mockExchangeWithException(URL, new ConnectException());

        StepVerifier
                .create(this.integration.call(getDestinationResponse(new URL(URL), TokenTypeCode.BASIC),
                        Mono.just(this.request)))
                .expectError(ServiceUnavailableFailedHttpResponseException.class).verify();
    }

    @Test
    public void testPartnerResponseTooLarge() throws MalformedURLException {
        mockResponseThrowsException(URL, HttpStatus.OK, new DataBufferLimitException("Limit Exceeded"));

        StepVerifier.create(this.integration.call(getDestinationResponse(new URL(URL), TokenTypeCode.BASIC),
                Mono.just(this.request))).expectError(PartnerResponseTooLargeException.class).verify();
    }

    private void verifyCallTimesHTTPHeaders(int timesBasicAuthorization, int timesBearerAuthorization,
            int timesCacheControl, int timesCorrelationId) {
        Mockito.verify(requestBodyMock, Mockito.times(timesBasicAuthorization)).header(eq(HttpHeaders.AUTHORIZATION),
                eq(HTTP_HEADER_BASIC_AUTHORIZATION));
        Mockito.verify(requestBodyMock, Mockito.times(timesBearerAuthorization)).header(eq(HttpHeaders.AUTHORIZATION),
                eq(HTTP_HEADER_BEARER_AUTHORIZATION));
        Mockito.verify(requestBodyMock, Mockito.times(timesCacheControl)).header(eq(HttpHeaders.CACHE_CONTROL),
                eq(HTTP_HEADER_CACHE_CONTROL));
        Mockito.verify(requestBodyMock, Mockito.times(timesCorrelationId))
                .header(eq(CorrelationIdHeaderService.HTTP_HEADER_CORRELATION_ID), eq(DEFAULT_CORRELATION_ID));
    }

    private TaxRequest getDefaultRequest() {

        List<Location> locations = new ArrayList<>();
        Location location;
        location = Location.builder().withType(LocationType.SHIP_FROM).withAddressLine1("9 High Street")
                .withZipCode("SW1A 2AA").withCity("London").withState("LND").withCountry(CountryCode.GB).build();
        locations.add(location);
        location = Location.builder().withType(LocationType.SHIP_TO).withAddressLine1("47 King William St")
                .withZipCode("EC4R 9AF").withCity("London").withState("LND").withCountry(CountryCode.GB).build();
        locations.add(location);

        ExemptionDetail exemptionDetail = ExemptionDetail.builder().build();
        List<ExemptionDetail> exemptionDetails = new ArrayList<>();
        exemptionDetails.add(exemptionDetail);

        Item item = Item.builder().withId("1").withItemCode("P1234353453").withItemType(ItemType.m)
                .withQuantity(new BigDecimal(1)).withUnitPrice(new BigDecimal(100))
                .withExemptionDetails(exemptionDetails).build();

        return TaxRequest.builder().withId("1").withDate(new Date()).withSaleOrPurchase(SaleOrPurchase.s)
                .withGrossOrNet(GrossOrNet.n).withCurrency(CurrencyCode.GBP).withItems(item).withLocations(locations)
                .build();

    }

    private void initMock(String url) {
        Mockito.when(this.webClientMock.post()).thenReturn(this.requestBodyUriMock);
        Mockito.when(this.requestBodyUriMock.uri(url)).thenReturn(this.requestBodyMock);
        Mockito.when(this.requestBodyMock.accept(MediaType.APPLICATION_JSON)).thenReturn(this.requestBodyMock);
        Mockito.when(this.requestBodyMock.header(any(), any())).thenReturn(requestBodyMock);
        Mockito.when(this.requestBodyMock.body(any(), any(Class.class))).thenReturn(this.requestHeadersMock);
    }

    private <T> void mockHttpResponse(String url, HttpStatus status, T response) {
        initMock(url);
        Mockito.when(this.requestHeadersMock.exchange()).thenReturn(Mono.just(this.responseMock));
        Mockito.when(this.responseMock.statusCode()).thenReturn(status);
        Mockito.when(this.responseMock.bodyToMono(any(Class.class))).thenReturn(Mono.just(response));
    }

    private <T> void mockResponseThrowsException(String url, HttpStatus status, Exception exception) {
        initMock(url);
        Mockito.when(this.requestHeadersMock.exchange()).thenReturn(Mono.just(this.responseMock));
        Mockito.when(this.responseMock.statusCode()).thenReturn(status);
        Mockito.when(this.responseMock.bodyToMono(any(Class.class))).thenReturn(Mono.error(exception));
    }

    private void mockExchangeWithException(String url, Exception exception) {
        initMock(url);
        Mockito.when(this.requestHeadersMock.exchange()).thenAnswer(invocation -> {
            throw exception;
        });
    }

    private DestinationResponse getDestinationResponseWithoutAuthentication() throws MalformedURLException {
        URL url = new URL("http://taas.taxweb.com.br:9090/taxgateway/webapi/taxrules/calctaxessap");
        return DestinationResponse.builder().withName(DEFAULT_DESTINATION).withUrl(url).build();
    }

    private DestinationResponse getDestinationResponse(URL url, TokenTypeCode authType) throws MalformedURLException {
        return DestinationResponse.builder().withName(DEFAULT_DESTINATION).withUrl(url)
                .withAuthorization(
                        DestinationAuthorization.builder().withTokenTypeCode(authType).withToken(TOKEN).build())
                .build();
    }

}
