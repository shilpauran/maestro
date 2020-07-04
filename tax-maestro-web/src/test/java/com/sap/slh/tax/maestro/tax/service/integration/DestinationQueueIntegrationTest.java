package com.sap.slh.tax.maestro.tax.service.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Locale;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.http.HttpStatus;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.net.HttpHeaders;
import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationRequest;
import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationResponse;
import com.sap.slh.tax.destinationconfiguration.destinations.dto.ErrorResponse;
import com.sap.slh.tax.destinationconfiguration.destinations.models.CountryRegionCode;
import com.sap.slh.tax.destinationconfiguration.destinations.models.TaxOperationCode;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument;
import com.sap.slh.tax.maestro.context.RequestContextService;
import com.sap.slh.tax.maestro.tax.exceptions.DestinationBadRequestException;
import com.sap.slh.tax.maestro.tax.exceptions.NoAmqpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.QueueCommunicationException;
import com.sap.slh.tax.maestro.tax.exceptions.UnknownAmqpResponseTypeException;
import com.sap.slh.tax.maestro.tax.jmx.DestinationQueueIntegrationErrorMBean;
import com.sap.slh.tax.maestro.tax.models.DestinationCacheKey;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(MockitoJUnitRunner.class)
public class DestinationQueueIntegrationTest {

    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private Function<QuoteDocument, Mono<DestinationRequest>> mapRequest;
    @Mock
    private Function<DestinationResponse, Mono<DestinationResponse>> mapResponse;

    private Jackson2JsonMessageConverter msgConverter = new Jackson2JsonMessageConverter();
    private RequestContextService requestContextService = Mockito.mock(RequestContextService.class);
    private Cache<DestinationCacheKey, DestinationResponse> cacheDestination;
    private String defaultTenant = "tenant1";
    private Locale defaultLocale = Locale.ENGLISH;
    private String defaultJwt = "jwt";
    private String defaultCorrelationId = "b03d17e7-480d-4f3b-9f3c-5307a0d27321";

    private DestinationQueueIntegration integration;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        cacheDestination = Caffeine.newBuilder().expireAfterWrite(Duration.ofSeconds(86400)).build();
        integration = new DestinationQueueIntegration(rabbitTemplate, msgConverter, cacheDestination,
                requestContextService);
        integration.destinationErrorMBean = new DestinationQueueIntegrationErrorMBean();

        Mockito.when(requestContextService.getCorrelationId()).thenReturn(Mono.just(defaultCorrelationId));
        Mockito.when(requestContextService.getTenantId()).thenReturn(Mono.just(defaultTenant));
        Mockito.when(requestContextService.getJwt()).thenReturn(Mono.just(defaultJwt));
        Mockito.when(requestContextService.getLocale()).thenReturn(Mono.just(defaultLocale));
        Mockito.when(requestContextService.shouldUseCache()).thenReturn(Mono.just(true));
    }

    @Test
    public void testCallDestinationQueueIntegrationWithEmptyCache() throws Exception {
        DestinationRequest destinationRequest = getDestinationRequest(CountryRegionCode.CA);
        DestinationResponse destinationResponse = getPartnerDestinationResponse();
        DestinationCacheKey destinationCacheKey = getDestinationCacheKey(destinationRequest);

        Message destinationResponseMsg = msgConverter.toMessage(destinationResponse, new MessageProperties(),
                DestinationResponse.class);

        mockSendAndReceive(destinationResponseMsg);
        mockMapperResponse(destinationRequest, destinationResponse);

        StepVerifier.create(integration.call(Mono.just(QuoteDocument.builder().build()), mapRequest, mapResponse))
                .consumeNextWith((output -> {
                    assertEquals(destinationResponse, output);
                    assertCacheDestination(destinationCacheKey, output, 1);
                    assertEquals(0, integration.destinationErrorMBean.getCount());
                    this.verifyMessageHeaderAttributes(
                            "com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationRequest",
                            defaultLocale.toLanguageTag(), defaultJwt, defaultCorrelationId);
                })).verifyComplete();
    }

    @Test
    public void testCallDestinationQueueIntegrationWithUsingCache() throws Exception {
        DestinationRequest destinationRequest = getDestinationRequest(CountryRegionCode.CA);
        DestinationResponse destinationResponse = getPartnerDestinationResponse();
        DestinationCacheKey destinationCacheKey = getDestinationCacheKey(destinationRequest);

        cacheDestination.put(getDestinationCacheKey(getDestinationRequest(CountryRegionCode.GB)),
                getTaxServiceDestinationResponse());
        cacheDestination.put(getDestinationCacheKey(getDestinationRequest(CountryRegionCode.CA)),
                getPartnerDestinationResponse());
        cacheDestination.cleanUp();

        mockMapperResponse(destinationRequest, destinationResponse);

        StepVerifier.create(integration.call(Mono.just(QuoteDocument.builder().build()), mapRequest, mapResponse))
                .consumeNextWith((output -> {
                    assertEquals(destinationResponse, output);
                    assertCacheDestination(destinationCacheKey, output, 2);
                    assertEquals(0, integration.destinationErrorMBean.getCount());
                })).verifyComplete();
    }

    @Test
    public void testCallDestinationQueueIntegrationWithNoCacheHeader() throws Exception {
        Mockito.when(requestContextService.shouldUseCache()).thenReturn(Mono.just(false));
        DestinationRequest destinationRequest = getDestinationRequest(CountryRegionCode.CA);
        DestinationResponse destinationResponse = getPartnerDestinationResponse();
        DestinationResponse destinationResponseInCache = getTaxServiceDestinationResponse();
        DestinationCacheKey destinationCacheKey = getDestinationCacheKey(destinationRequest);

        cacheDestination.put(destinationCacheKey, destinationResponseInCache);
        cacheDestination.cleanUp();

        Message destinationResponseMsg = msgConverter.toMessage(destinationResponse, new MessageProperties(),
                DestinationResponse.class);

        mockSendAndReceive(destinationResponseMsg);
        mockMapperResponse(destinationRequest, destinationResponse);

        StepVerifier.create(integration.call(Mono.just(QuoteDocument.builder().build()), mapRequest, mapResponse))
                .consumeNextWith((output -> {
                    assertEquals(destinationResponse, output);
                    assertCacheDestination(destinationCacheKey, destinationResponse, 1);
                    assertEquals(0, integration.destinationErrorMBean.getCount());
                    this.verifyMessageHeaderAttributes(
                            "com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationRequest",
                            defaultLocale.toLanguageTag(), defaultJwt, defaultCorrelationId);
                })).verifyComplete();
    }

    @Test
    public void testCallDestinationQueueIntegrationWithNoResponse() {
        StepVerifier.create(integration.call(Mono.just(getDestinationRequest(CountryRegionCode.CA))))
                .consumeErrorWith(exception -> {
                    assertTrue(exception instanceof NoAmqpResponseException);
                    assertEquals(1, integration.destinationErrorMBean.getCount());
                }).verify();
    }

    @Test
    public void testCallDestinationQueueIntegrationWithDestinationNotFound() {
        DestinationRequest destinationRequest = getDestinationRequest(CountryRegionCode.CA);
        DestinationCacheKey destinationCacheKey = getDestinationCacheKey(destinationRequest);

        ErrorResponse errorResponse = ErrorResponse.builder().withStatus(HttpStatus.NO_CONTENT.value()).build();
        Message destinationResponseMsg = msgConverter.toMessage(errorResponse, new MessageProperties(),
                ErrorResponse.class);

        mockSendAndReceive(destinationResponseMsg);

        StepVerifier.create(integration.call(Mono.just(destinationRequest))).consumeNextWith((output -> {
            assertEquals(output, getTaxServiceDestinationResponse());
            assertCacheDestination(destinationCacheKey, output, 1);
            assertEquals(0, integration.destinationErrorMBean.getCount());
            this.verifyMessageHeaderAttributes(
                    "com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationRequest",
                    defaultLocale.toLanguageTag(), defaultJwt, defaultCorrelationId);
        })).verifyComplete();
    }

    @Test
    public void testCallDestinationQueueIntegrationWithDestinationBadRequest() {
        DestinationRequest destinationRequest = getDestinationRequest(CountryRegionCode.CA);
        DestinationCacheKey destinationCacheKey = getDestinationCacheKey(destinationRequest);

        ErrorResponse errorResponse = ErrorResponse.builder().withStatus(HttpStatus.BAD_REQUEST.value()).build();

        Message destinationResponseMsg = msgConverter.toMessage(errorResponse, new MessageProperties(),
                ErrorResponse.class);

        mockSendAndReceive(destinationResponseMsg);

        StepVerifier.create(integration.call(Mono.just(destinationRequest))).consumeErrorWith(exception -> {
            assertTrue(exception instanceof DestinationBadRequestException);
            assertCacheDestination(destinationCacheKey, null, 0);
            assertEquals(0, integration.destinationErrorMBean.getCount());
        }).verify();
    }

    @Test
    public void testCallDestinationQueueIntegrationWithError() {
        DestinationRequest destinationRequest = getDestinationRequest(CountryRegionCode.CA);
        DestinationCacheKey destinationCacheKey = getDestinationCacheKey(destinationRequest);

        ErrorResponse errorResponse = ErrorResponse.builder().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
        Message destinationResponseMsg = msgConverter.toMessage(errorResponse, new MessageProperties(),
                ErrorResponse.class);

        mockSendAndReceive(destinationResponseMsg);

        StepVerifier.create(integration.call(Mono.just(destinationRequest))).consumeErrorWith(exception -> {
            assertTrue(exception instanceof QueueCommunicationException);
            assertCacheDestination(destinationCacheKey, null, 0);
            assertEquals(0, integration.destinationErrorMBean.getCount());
        }).verify();
    }

    @Test
    public void testCallDestinationQueueIntegrationWithUnknownResponse() {
        Message errorMessage = msgConverter.toMessage("unexpected", new MessageProperties(), String.class);
        mockSendAndReceive(errorMessage);

        StepVerifier.create(integration.call(Mono.just(getDestinationRequest(CountryRegionCode.CA))))
                .consumeErrorWith(exception -> {
                    assertTrue(exception instanceof UnknownAmqpResponseTypeException);
                    assertEquals(0, integration.destinationErrorMBean.getCount());
                }).verify();

    }

    private void mockSendAndReceive(Message returnMessage) {
        Mockito.when(rabbitTemplate.sendAndReceive(Mockito.anyString(), Mockito.anyString(), any(Message.class)))
                .thenReturn(returnMessage);
    }

    private DestinationResponse getTaxServiceDestinationResponse() {
        return DestinationResponse.builder().withName("tax-service").withValidity(86400).build();
    }

    private DestinationResponse getPartnerDestinationResponse() throws MalformedURLException {
        return DestinationResponse.builder().withName("VERTEX-CA").withTaxOperationCode(TaxOperationCode.QUOTE)
                .withUrl(new URL("http://connector-dev.vertexconnectors.com:8098/sap-tax-service/TaxService/quote"))
                .withValidity(86400).build();
    }

    private void mockMapperResponse(DestinationRequest destinationRequest, DestinationResponse response) {
        Mockito.when(mapRequest.apply(any(QuoteDocument.class))).thenReturn(Mono.just(destinationRequest));
        Mockito.when(mapResponse.apply(any(DestinationResponse.class))).thenReturn(Mono.just(response));
    }

    private void verifyMessageHeaderAttributes(String objectType, String locale, String jwt, String correlationId) {
        Mockito.verify(rabbitTemplate).sendAndReceive(any(String.class), any(String.class), argThat((Message msg) -> {
            return msg.getMessageProperties().getHeaders().get("__TypeId__") == objectType
                    && msg.getMessageProperties().getHeaders().get(HttpHeaders.ACCEPT_LANGUAGE) == locale
                    && msg.getMessageProperties().getHeaders().get(HttpHeaders.AUTHORIZATION) == jwt
                    && msg.getMessageProperties().getHeaders()
                            .get(QueueIntegration.HEADER_CORRELATION_ID) == correlationId;

        }));
    }

    private void assertCacheDestination(DestinationCacheKey key, DestinationResponse expectedValue, long cacheSize) {
        cacheDestination.cleanUp();
        assertEquals(expectedValue, cacheDestination.getIfPresent(key));
        assertEquals(cacheSize, cacheDestination.estimatedSize());
    }

    private DestinationRequest getDestinationRequest(CountryRegionCode countryRegionCode) {
        return DestinationRequest.builder().withCountryRegionCode(countryRegionCode).build();
    }

    private DestinationCacheKey getDestinationCacheKey(DestinationRequest destinationRequest) {
        return DestinationCacheKey.builder().withTenantId(defaultTenant).withDestinationRequest(destinationRequest)
                .build();
    }

}
