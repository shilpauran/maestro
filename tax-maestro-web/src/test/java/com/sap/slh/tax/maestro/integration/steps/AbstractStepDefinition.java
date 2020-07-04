package com.sap.slh.tax.maestro.integration.steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

import java.io.IOException;

import com.sap.slh.tax.maestro.context.correlation.CorrelationIdValidator;
import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.sap.cloud.security.xsuaa.XsuaaServiceConfiguration;
import com.sap.cloud.security.xsuaa.test.JwtGenerator;
import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationRequest;
import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationResponse;
import com.sap.slh.tax.destinationconfiguration.destinations.dto.ErrorResponse;
import com.sap.slh.tax.maestro.api.v0.schema.TaxResponse;
import com.sap.slh.tax.maestro.integration.steps.TestDataLoader.ScenarioType;
import com.sap.slh.tax.maestro.tax.jmx.CalculateQueueIntegrationErrorMBean;
import com.sap.slh.tax.maestro.tax.jmx.DestinationQueueIntegrationErrorMBean;
import com.sap.slh.tax.maestro.tax.jmx.DetermineQueueIntegrationErrorMBean;
import com.sap.slh.tax.maestro.tax.jmx.EnhanceQueueIntegrationErrorMBean;
import com.sap.slh.tax.maestro.tax.jmx.QuoteHttpIntegrationMBeansHandler;
import com.sap.slh.tax.maestro.tax.models.DestinationCacheKey;
import com.sap.slh.tax.product.tax.classification.models.ProductClassifications;

import reactor.core.publisher.Mono;

public abstract class AbstractStepDefinition {

    protected static final String XSUAA_SUBDOMAIN = "testdomain";
    protected static final String EXCHANGE_NAME = "txs.engine.TAXSERVICE";
    protected static final String EXCHANGE_NAME_ENHANCER = "txs.tax-classifier";
    protected static final String EXCHANGE_NAME_DESTINATION = "txs.tax-destination-service";
    protected static final String ROUTING_KEY_CALCULATION = ".txs.tax.v1.calculate";
    protected static final String ROUTING_KEY_DETERMINATION = ".txs.tax.v1.determine";
    protected static final String ROUTING_KEY_ENHANCER = ".txs.tax-classifier.product-classification";
    protected static final String ROUTING_KEY_DESTINATION = ".txs.tax-destination-service.get";
    protected static final String DEFAULT_TENANT = "uaa";
    protected static final String PING_ENDPOINT = "/tax/ping";
    protected static final Integer STATUS_OK = HttpStatus.OK.value();
    protected static final Integer STATUS_BAD_REQUEST = HttpStatus.BAD_REQUEST.value();
    protected static final Integer STATUS_UNAUTHORIZED = HttpStatus.UNAUTHORIZED.value();
    protected static final Integer STATUS_NOT_FOUND = HttpStatus.NOT_FOUND.value();
    protected static final Integer STATUS_METHOD_NOT_ALLOWED = HttpStatus.METHOD_NOT_ALLOWED.value();
    protected static final Integer STATUS_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR.value();
    protected static final Integer NO_CONTENT_FOUND = HttpStatus.NO_CONTENT.value();
    protected static final Integer PARTIAL_CONTENT_FOUND = HttpStatus.PARTIAL_CONTENT.value();
    protected static final Integer PAYLOAD_TOO_LARGE = HttpStatus.PAYLOAD_TOO_LARGE.value();
    protected static final String CORRELATION_ID_HEADER_NAME = "X-CorrelationID";
    protected static final String VCAP_REQUEST_ID_HEADER_NAME = "X-Vcap-Request-Id";
    protected static final String DUMMY_CORRELATION_ID = "b03d17e7-480d-4f3b-9f3c-5307a0d27321";
    protected static final String DUMMY_VCAP_REQUEST_ID = "4d477cf7-2df2-4e2e-9393-e776dee723eb";
    protected static final String CORRELATION_ID_TOO_LARGE = "b03d17e7-480d-4f3b-9f3c-5307a0d27321-480059bf-2b24-4ba6-ba24-b76777da43f8-5307a0d27321";
    protected static final String INVALID_CORRELATION_ID = "<...>";
    protected static final String CORRELATION_ID_PATTERN = "^[0-9a-zA-Z\\-]{1,72}+$";
    
    @Autowired
    protected WebTestClient webTestClient;
    @Autowired
    @Qualifier("xsuaaMockServiceConfig")
    protected XsuaaServiceConfiguration xsuaaMockServiceConfig;
    @Autowired
    protected RabbitTemplate rabbitTemplate;
    @Autowired
    protected Cache<DestinationCacheKey, DestinationResponse> cacheDestination;
    @Autowired
    protected Jackson2JsonMessageConverter msgConverter;
    @Autowired
    protected ObjectMapper mapper;
    @Autowired
    protected WebClient partnerWebClient;
    @Autowired
    protected EnhanceQueueIntegrationErrorMBean jxmEnhanceBean;
    @Autowired
    protected DestinationQueueIntegrationErrorMBean jxmDestinationBean;
    @Autowired
    protected DetermineQueueIntegrationErrorMBean jxmDetermineBean;
    @Autowired
    protected CalculateQueueIntegrationErrorMBean jxmCalculateBean;
    @Autowired
    protected QuoteHttpIntegrationMBeansHandler quoteMBeansHandler;
    @Autowired
    protected CorrelationIdValidator correlationIdValidator;

    protected WebClient.RequestBodyUriSpec requestBodyUriSpecMock;
    protected WebClient.RequestHeadersSpec requestHeadersSpecMock;
    protected WebClient.RequestBodySpec requestBodySpecMock;
    protected ClientResponse responseSpecMock;

    protected WebTestClient.ResponseSpec responseSpec;
    protected WebTestClient.RequestBodySpec requestBodySpec;
    protected WebTestClient.RequestHeadersSpec<?> requestHeaderSpec;
    private String currentTenant;
    protected String apiVersion;

    protected TestDataLoader dataLoader = TestDataLoader.getInstance();

    protected void mockSendAndReceive(String exchange, String routingKey, Message returnMessage) {
        Mockito.when(rabbitTemplate.sendAndReceive(eq(exchange), eq(routingKey), any(Message.class)))
                .thenReturn(returnMessage);
    }

    protected void mockPartnerWebClientRequest() {    
        requestBodyUriSpecMock = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        requestHeadersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);
        requestBodySpecMock = Mockito.mock(WebClient.RequestBodySpec.class);
        responseSpecMock = Mockito.mock(ClientResponse.class);

        Mockito.when(partnerWebClient.post()).thenReturn(requestBodyUriSpecMock);
        Mockito.when(requestBodyUriSpecMock.uri(any(String.class))).thenReturn(requestBodySpecMock);
        Mockito.when(requestBodySpecMock.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpecMock);
        Mockito.when(requestBodySpecMock.header(any(), any())).thenReturn(requestBodySpecMock);
        Mockito.when(requestBodySpecMock.body(any(), any(Class.class))).thenReturn(requestHeadersSpecMock);
    }

    private void initiateMockPartnerWebClientResponse(HttpStatus status) {
        Mockito.when(requestHeadersSpecMock.exchange()).thenReturn(Mono.just(responseSpecMock));
        Mockito.when(responseSpecMock.statusCode()).thenReturn(status);
    }

    protected void mockPartnerWebClientResponse(Object response) {
        initiateMockPartnerWebClientResponse(HttpStatus.OK);
        Mockito.when(responseSpecMock.bodyToMono(any(Class.class))).thenReturn(Mono.just(response));
    }

    protected void mockResponseThrowsException(Exception exception, HttpStatus status) {
        initiateMockPartnerWebClientResponse(status);
        Mockito.when(responseSpecMock.bodyToMono(any(Class.class))).thenReturn(Mono.error(exception));
    }

    protected void mockPartnerResponseStatus(HttpStatus status) {
        Mockito.when(responseSpecMock.statusCode()).thenReturn(status);
    }

    protected JwtGenerator getJwtGenerator(String tenant) {
        String scope = xsuaaMockServiceConfig.getAppId() + ".Display";
        JwtGenerator jwtGenerator;
        if (StringUtils.isEmpty(tenant)) {
            jwtGenerator = new JwtGenerator();
        } else {
            jwtGenerator = new JwtGenerator(JwtGenerator.CLIENT_ID, XSUAA_SUBDOMAIN, tenant);
            setCurrentTenant(tenant);
        }
        return jwtGenerator.addScopes(scope);
    }

    protected String getCurrentTenant() {
        if (StringUtils.isEmpty(this.currentTenant)) {
            return DEFAULT_TENANT;
        }
        return this.currentTenant;
    }

    protected void setCurrentTenant(String tenant) {
        this.currentTenant = tenant;
    }

    protected void setMethodAndPath(HttpMethod method, String path) {
        this.requestBodySpec = this.webTestClient.method(method).uri(path);
    }

    protected void verifySendReceiveCalls(int callCount, String exchange, String routingKey) {
        Mockito.verify(rabbitTemplate, Mockito.times(callCount)).sendAndReceive(eq(exchange), eq(routingKey),
                any(Message.class));
    }

    protected void hasQueueMessageProperty(String exchange, String routingKey, String headerKey, String headerValue) {
        Mockito.verify(rabbitTemplate).sendAndReceive(eq(exchange), eq(getCurrentTenant() + routingKey),
                argThat((Message msg) -> {
                    try {
                        Object actualLocale = msg.getMessageProperties().getHeaders().get(headerKey).toString();
                        ObjectVerification.verifyEqualsString(headerValue, actualLocale);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }));
    }

    protected void hasQueueMessagePropertyCorrelationId(String exchange, String routingKey) {
        Mockito.verify(rabbitTemplate).sendAndReceive(eq(exchange), eq(getCurrentTenant() + routingKey),
                argThat((Message msg) -> {
                    try {
                        String correlationId = msg.getMessageProperties()
                                .getHeaders()
                                .get(CORRELATION_ID_HEADER_NAME)
                                .toString();
                        this.verifyHeader(CORRELATION_ID_HEADER_NAME, correlationId);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }));
    }
    
    protected void verifyStatusCode(Integer statusCode) {
        responseSpec.expectStatus().isEqualTo(statusCode);
    }
    
    protected void verifyHeader(String name, String value) {
        responseSpec.expectHeader().valueEquals(name, value);
    }
    
    protected void verifyHeaderMatches(String name, String value) {
        responseSpec.expectHeader().valueMatches(name, value);
    }

    protected Message convertJsonToDestinationResponse(String body) throws Exception {
        Message destinationMsg;
        DestinationResponse destinationResponse;

        destinationResponse = mapper.readValue(body, DestinationResponse.class);

        if (destinationResponse.getName() != null) {
            destinationMsg = msgConverter.toMessage(destinationResponse, new MessageProperties());
            return destinationMsg;
        } else {
            throw new Exception("Couldn't parse valid DestinationResponse object");
        }
    }

    protected Message convertJsonToErrorResponse(String body) throws Exception {
        Message destinationMsg;
        ErrorResponse errorResponse;

        errorResponse = mapper.readValue(body, ErrorResponse.class);

        if (errorResponse.getStatus() != null) {
            destinationMsg = msgConverter.toMessage(mapper.readValue(body, ErrorResponse.class),
                    new MessageProperties());
            return destinationMsg;
        } else {
            throw new Exception("Couldn't parse valid ErrorResponse object");
        }
    }

    protected void convertAndMockEnhanceResponse(String body)
            throws JsonParseException, JsonMappingException, IOException {
        Message enhanceMsg = null;
        ProductClassifications productClassification = mapper.readValue(body, ProductClassifications.class);
        if (productClassification.getProductClassifications() != null) {
            enhanceMsg = msgConverter.toMessage(productClassification, new MessageProperties());
        } else {
            com.sap.slh.tax.product.tax.classification.models.ErrorResponse errorResponse = mapper.readValue(body,
                    com.sap.slh.tax.product.tax.classification.models.ErrorResponse.class);
            enhanceMsg = msgConverter.toMessage(errorResponse, new MessageProperties());
        }

        this.mockSendAndReceive(EXCHANGE_NAME_ENHANCER, getCurrentTenant() + ROUTING_KEY_ENHANCER, enhanceMsg);
    }

    protected void convertAndMockV0PartnerWebClientResponse(String body) throws IOException {
        TaxResponse taxResponse = mapper.readValue(body, TaxResponse.class);
        if (taxResponse.getDate() != null) {
            this.mockPartnerWebClientResponse(taxResponse);
        } else {
            com.sap.slh.tax.maestro.api.v0.schema.ErrorResponse errorResponse = mapper.readValue(body,
                    com.sap.slh.tax.maestro.api.v0.schema.ErrorResponse.class);
            this.mockPartnerWebClientResponse(errorResponse);
        }
    }

    protected DestinationCacheKey getDestinationCacheKey(String scenario) throws IOException {
        String body = dataLoader.loadScenario(scenario, ScenarioType.DESTINATION_REQUEST);

        if (body != null) {
            return DestinationCacheKey.builder()
                    .withTenantId(getCurrentTenant())
                    .withDestinationRequest(mapper.readValue(body, DestinationRequest.class))
                    .build();
        }

        return null;
    }

    protected DestinationResponse getDestinationCacheValue(String scenario) throws IOException {
        String body = dataLoader.loadScenario(scenario, ScenarioType.DESTINATION_CACHE_RESPONSE);

        if (body != null) {
            return mapper.readValue(body, DestinationResponse.class);
        }

        return null;
    }

}
