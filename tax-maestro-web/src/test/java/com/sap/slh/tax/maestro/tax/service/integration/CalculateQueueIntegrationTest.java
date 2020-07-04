package com.sap.slh.tax.maestro.tax.service.integration;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import com.google.common.net.HttpHeaders;
import com.sap.slh.tax.attributes.determination.model.api.ProcessingStatusCode;
import com.sap.slh.tax.attributes.determination.model.api.Status;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponse;
import com.sap.slh.tax.attributes.determination.model.response.TaxAttributesDeterminationResponseModel;
import com.sap.slh.tax.attributes.determination.model.response.TaxLine;
import com.sap.slh.tax.calculation.model.common.TaxCalculationApplicationError;
import com.sap.slh.tax.calculation.model.common.TaxCalculationErrorDetail;
import com.sap.slh.tax.calculation.model.common.TaxCalculationErrorMessage;
import com.sap.slh.tax.calculation.model.common.TaxCalculationProcessingStatusCode;
import com.sap.slh.tax.calculation.model.common.TaxCalculationRequest;
import com.sap.slh.tax.calculation.model.common.TaxCalculationResponse;
import com.sap.slh.tax.calculation.model.common.TaxCalculationStatus;
import com.sap.slh.tax.maestro.api.common.domain.CurrencyCode;
import com.sap.slh.tax.maestro.api.v1.domain.AmountType;
import com.sap.slh.tax.maestro.api.v1.schema.Item;
import com.sap.slh.tax.maestro.api.v1.schema.QuoteDocument;
import com.sap.slh.tax.maestro.context.RequestContextService;
import com.sap.slh.tax.maestro.tax.exceptions.NoAmqpResponseException;
import com.sap.slh.tax.maestro.tax.exceptions.QueueCommunicationException;
import com.sap.slh.tax.maestro.tax.exceptions.UnknownAmqpResponseTypeException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.CalculateInvalidModelException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.CalculateNoContentException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.CalculatePartialContentException;
import com.sap.slh.tax.maestro.tax.exceptions.calculate.ErrorDetail;
import com.sap.slh.tax.maestro.tax.jmx.CalculateQueueIntegrationErrorMBean;
import com.sap.slh.tax.maestro.tax.mapper.DeterminationResponseQuoteDocumentToCalculationRequestMapper;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class CalculateQueueIntegrationTest {

    private RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
    private Jackson2JsonMessageConverter msgConverter = new Jackson2JsonMessageConverter();
    private RequestContextService requestContextService = Mockito.mock(RequestContextService.class);
    private String defaultTenant = "tenant1";
    private String defaultJwt = "jwt";
    private Locale defaultLocale = Locale.ENGLISH;
    private String defaultCorrelationId = "b03d17e7-480d-4f3b-9f3c-5307a0d27321";

    private DeterminationResponseQuoteDocumentToCalculationRequestMapper mapper = DeterminationResponseQuoteDocumentToCalculationRequestMapper
            .getInstance();

    private CalculateQueueIntegration integration;

    @Before
    public void setup() {
        integration = new CalculateQueueIntegration(rabbitTemplate, msgConverter, requestContextService);
        integration.calculateErrorMBean = new CalculateQueueIntegrationErrorMBean();

        Mockito.when(requestContextService.getCorrelationId()).thenReturn(Mono.just(defaultCorrelationId));
        Mockito.when(requestContextService.getTenantId()).thenReturn(Mono.just(defaultTenant));
        Mockito.when(requestContextService.getJwt()).thenReturn(Mono.just(defaultJwt));
        Mockito.when(requestContextService.getLocale()).thenReturn(Mono.just(defaultLocale));
    }

    @Test
    public void testCallCalculateQueueIntegration() {
        TaxCalculationResponse calculateResponse = buildCalculateResponse();
        Message calculateResponsedMsg = msgConverter.toMessage(calculateResponse, new MessageProperties(),
                TaxCalculationResponse.class);

        mockSendAndReceive(calculateResponsedMsg);

        StepVerifier
                .create(integration.call(Mono.just(buildDetermineResponse()), Mono.just(buildQuoteDocument()), mapper))
                .consumeNextWith((output -> {
                    assertEquals(TaxCalculationStatus.SUCCESS, output.getStatus());
                    assertEquals(0, integration.calculateErrorMBean.getCount());
                    this.verifyMessageHeaderAttributes("com.sap.slh.tax.calculation.model.common.TaxCalculationRequest",
                            defaultLocale.toLanguageTag(), defaultJwt, defaultCorrelationId);
                })).verifyComplete();
    }

    @Test
    public void testCallCalculateQueueIntegrationWithNoResponse() {
        StepVerifier.create(integration.call(Mono.just(new TaxCalculationRequest())))
                .expectError(NoAmqpResponseException.class).verify();
        assertEquals(1, integration.calculateErrorMBean.getCount());
    }

    @Test
    public void testCallCalculateQueueIntegrationWithError() {
        TaxCalculationResponse calculateResponse = buildErrorCalculateResponse(TaxCalculationStatus.FAILURE,
                TaxCalculationProcessingStatusCode.TAX_CALCULATION_FAILURE);
        Message calculateResponsedMsg = msgConverter.toMessage(calculateResponse, new MessageProperties(),
                TaxCalculationResponse.class);

        mockSendAndReceive(calculateResponsedMsg);

        StepVerifier
                .create(integration.call(Mono.just(buildDetermineResponse()), Mono.just(buildQuoteDocument()), mapper))
                .expectError(QueueCommunicationException.class).verify();
        assertEquals(0, integration.calculateErrorMBean.getCount());
    }

    @Test
    public void testCallCalculateQueueIntegrationWithNoContentError() {
        TaxCalculationResponse calculateResponse = buildErrorCalculateResponse(TaxCalculationStatus.NO_CONTENT,
                TaxCalculationProcessingStatusCode.CONTENT_NOT_FOUND);
        Message calculateResponsedMsg = msgConverter.toMessage(calculateResponse, new MessageProperties(),
                TaxCalculationResponse.class);

        mockSendAndReceive(calculateResponsedMsg);

        StepVerifier
                .create(integration.call(Mono.just(buildDetermineResponse()), Mono.just(buildQuoteDocument()), mapper))
                .expectError(CalculateNoContentException.class).verify();
        assertEquals(0, integration.calculateErrorMBean.getCount());
    }

    @Test
    public void testCallCalculateQueueIntegrationWithPartialContentError() {
        TaxCalculationResponse calculateResponse = buildErrorCalculateResponse(TaxCalculationStatus.PARTIAL_CONTENT,
                TaxCalculationProcessingStatusCode.PARTIAL_CONTENT);
        Message calculateResponsedMsg = msgConverter.toMessage(calculateResponse, new MessageProperties());

        mockSendAndReceive(calculateResponsedMsg);

        StepVerifier
                .create(integration.call(Mono.just(buildDetermineResponse()), Mono.just(buildQuoteDocument()), mapper))
                .expectError(CalculatePartialContentException.class).verify();
        assertEquals(0, integration.calculateErrorMBean.getCount());
    }

    @Test
    public void testCallCalculateQueueIntegrationWithInvalidRequestError() {
        TaxCalculationResponse calculateResponse = buildErrorCalculateResponseWithDetails(
                TaxCalculationStatus.INVALID_REQUEST, TaxCalculationProcessingStatusCode.REQUEST_NOT_VALID);
        Message calculateResponsedMsg = msgConverter.toMessage(calculateResponse, new MessageProperties());

        mockSendAndReceive(calculateResponsedMsg);

        List<ErrorDetail> errorDetails = Arrays.asList(
                new ErrorDetail(TaxCalculationErrorMessage.ITEM_ID_EXISTS, "message1"),
                new ErrorDetail(TaxCalculationErrorMessage.DATE_INVALID, "message2"));

        CalculateInvalidModelException expectedException = new CalculateInvalidModelException(
                "Unable to calculate tax; check the log for details.", errorDetails);

        StepVerifier
                .create(integration.call(Mono.just(buildDetermineResponse()), Mono.just(buildQuoteDocument()), mapper))
                .consumeErrorWith(exception -> {
                    assertEquals(expectedException, exception);
                    assertEquals(0, integration.calculateErrorMBean.getCount());
                }).verify();
    }

    @Test
    public void testCallCalculateQueueIntegrationWithUnknownResponse() {
        Message calculateResponsedMsg = msgConverter.toMessage("unexpected", new MessageProperties(), String.class);

        mockSendAndReceive(calculateResponsedMsg);

        StepVerifier
                .create(integration.call(Mono.just(buildDetermineResponse()), Mono.just(buildQuoteDocument()), mapper))
                .expectError(UnknownAmqpResponseTypeException.class).verify();
        assertEquals(0, integration.calculateErrorMBean.getCount());
    }

    @Test
    public void testCallCalculateQueueIntegrationWithAggregate() {
        TaxCalculationResponse calculateResponse = buildCalculateResponse();
        Message calculateResponsedMsg = msgConverter.toMessage(calculateResponse, new MessageProperties(),
                TaxCalculationResponse.class);

        mockSendAndReceive(calculateResponsedMsg);

        StepVerifier
                .create(integration.call(Mono.just(buildDetermineResponse()), Mono.just(buildQuoteDocument()),
                        DeterminationResponseQuoteDocumentToCalculationRequestMapper.getInstance()))

                .consumeNextWith((output -> {
                    assertEquals(TaxCalculationStatus.SUCCESS, output.getStatus());
                    assertEquals(0, integration.calculateErrorMBean.getCount());
                    this.verifyMessageHeaderAttributes("com.sap.slh.tax.calculation.model.common.TaxCalculationRequest",
                            defaultLocale.toLanguageTag(), defaultJwt, defaultCorrelationId);
                })).verifyComplete();
    }

    private void mockSendAndReceive(Message returnMessage) {
        Mockito.when(rabbitTemplate.sendAndReceive(Mockito.anyString(), Mockito.anyString(), any(Message.class)))
                .thenReturn(returnMessage);
    }

    private TaxAttributesDeterminationResponseModel buildDetermineResponse() {
        TaxAttributesDeterminationResponseModel responseModel = new TaxAttributesDeterminationResponseModel();

        TaxAttributesDeterminationResponse detResponse = new TaxAttributesDeterminationResponse();
        detResponse.setId("parentID");

        TaxLine taxLine = new TaxLine();
        taxLine.setId("taxLineId");
        taxLine.setIsTaxDeferred(Boolean.FALSE);
        taxLine.setNonDeductibleTaxRate(BigDecimal.TEN);
        taxLine.setDueCategoryCode("PAYABLE");
        taxLine.setTaxTypeCode("taxTypeCode");

        com.sap.slh.tax.attributes.determination.model.response.ResponseItem item = new com.sap.slh.tax.attributes.determination.model.response.ResponseItem();
        item.setId("id");
        item.setCountryRegionCode("BR");
        item.setSubdivisionCode("state");
        item.setTaxCode("taxCode");
        item.setTaxEventLegalPhrase("legalPhrase");
        item.setTaxDeterminationDetails("details");
        item.setTaxEventCode("eventCode");
        item.setIsTaxEventNonTaxable(Boolean.FALSE);
        item.setTaxes(Arrays.asList(taxLine));

        detResponse.setItems(Arrays.asList(item));

        responseModel.setResult(detResponse);

        responseModel.setStatus(Status.SUCCESS);
        responseModel.setProcessingStatusCode(ProcessingStatusCode.TAX_ATTRIBUTES_FETCHED_SUCCESSFULLY);
        responseModel.setStatusMessage("Tax attributes fetched successfully");

        return responseModel;
    }

    private TaxCalculationResponse buildCalculateResponse() {
        TaxCalculationResponse taxCalculationResponse = new TaxCalculationResponse();
        taxCalculationResponse.setStatus(TaxCalculationStatus.SUCCESS);

        return taxCalculationResponse;
    }

    private TaxCalculationResponse buildErrorCalculateResponse(TaxCalculationStatus status,
            TaxCalculationProcessingStatusCode processingStatusCode) {
        TaxCalculationResponse calculationResponse = new TaxCalculationResponse();

        calculationResponse.setStatus(status);
        calculationResponse.setProcessingStatusCode(processingStatusCode);
        calculationResponse.setStatusMessage("Error");

        return calculationResponse;
    }

    private TaxCalculationResponse buildErrorCalculateResponseWithDetails(TaxCalculationStatus status,
            TaxCalculationProcessingStatusCode processingStatusCode) {
        TaxCalculationResponse calculationResponse = this.buildErrorCalculateResponse(status, processingStatusCode);

        TaxCalculationApplicationError calculationError = new TaxCalculationApplicationError();

        TaxCalculationErrorDetail detail1 = new TaxCalculationErrorDetail();
        detail1.setErrorCode(TaxCalculationErrorMessage.ITEM_ID_EXISTS);
        detail1.setMessage("message1");
        TaxCalculationErrorDetail detail2 = new TaxCalculationErrorDetail();
        detail2.setErrorCode(TaxCalculationErrorMessage.DATE_INVALID);
        detail2.setMessage("message2");

        List<TaxCalculationErrorDetail> details = Arrays.asList(detail1, detail2);
        calculationError.setDetails(details);

        calculationResponse.setError(calculationError);

        return calculationResponse;
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

    private QuoteDocument buildQuoteDocument() {
        return QuoteDocument.builder().withCurrencyCode(CurrencyCode.BRL).withAmountTypeCode(AmountType.GROSS)
                .withItems(
                        Item.builder().withId("id").withUnitPrice(BigDecimal.TEN).withQuantity(BigDecimal.TEN).build())
                .build();
    }
}
